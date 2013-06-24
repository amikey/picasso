/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Future;

import static com.squareup.picasso.Utils.createKey;

abstract class Request<T> implements Runnable {
  static final int DEFAULT_RETRY_COUNT = 2;

  static class RequestWeakReference<T> extends WeakReference<T> {
    final Request request;

    public RequestWeakReference(Request request, T referent, ReferenceQueue<? super T> q) {
      super(referent, q);
      this.request = request;
    }
  }

  final Picasso picasso;
  final Uri uri;
  final int resourceId;
  final WeakReference<T> target;
  final PicassoBitmapOptions options;
  final List<Transformation> transformations;
  final boolean skipCache;
  final boolean noFade;
  final int errorResId;
  final Drawable errorDrawable;
  final String key;

  Future<?> future;
  Bitmap result;
  LoadedFrom loadedFrom;
  int retryCount;
  boolean retryCancelled;

  Request(Picasso picasso, Uri uri, int resourceId, T target, PicassoBitmapOptions options,
      List<Transformation> transformations, boolean skipCache, boolean noFade, int errorResId,
      Drawable errorDrawable) {
    this.picasso = picasso;
    this.uri = uri;
    this.resourceId = resourceId;
    this.target = new RequestWeakReference<T>(this, target, picasso.referenceQueue);
    this.options = options;
    this.transformations = transformations;
    this.skipCache = skipCache;
    this.noFade = noFade;
    this.errorResId = errorResId;
    this.errorDrawable = errorDrawable;
    this.retryCount = DEFAULT_RETRY_COUNT;
    this.key = createKey(this);
  }

  @Override public final void run() {
    try {
      // Change the thread name to contain the target URL for debugging purposes.
      Thread.currentThread().setName(Utils.THREAD_PREFIX + getName());

      picasso.run(this);
    } catch (final Throwable e) {
      // If an unexpected exception happens, we should crash the app instead of letting the
      // executor swallow it.
      picasso.handler.post(new Runnable() {
        @Override public void run() {
          throw new RuntimeException("An unexpected exception occurred", e);
        }
      });
    } finally {
      Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
    }
  }

  T getTarget() {
    return target.get();
  }

  @Override public String toString() {
    return "Request["
        + "hashCode="
        + hashCode()
        + ", picasso="
        + picasso
        + ", uri="
        + uri
        + ", resourceId="
        + resourceId
        + ", target="
        + target
        + ", options="
        + options
        + ", transformations="
        + transformationKeys()
        + ", future="
        + future
        + ", result="
        + result
        + ", retryCount="
        + retryCount
        + ", loadedFrom="
        + loadedFrom
        + ']';
  }

  void retry() {
    if (retryCancelled) return;

    if (retryCount > 0) {
      retryCount--;
      picasso.submitWithTarget(this);
    } else {
      picasso.targetsToRequests.remove(target.get());
      error();
    }
  }

  void cancel(Uri uri) {
    if (!future.isDone()) {
      future.cancel(true);
    } else if (uri == null || !uri.equals(this.uri)) {
      retryCancelled = true;
    }
  }

  abstract void complete();

  abstract void error();

  String transformationKeys() {
    if (transformations == null) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder(transformations.size() * 16);

    sb.append('[');
    boolean first = true;
    for (Transformation transformation : transformations) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(transformation.key());
    }
    sb.append(']');

    return sb.toString();
  }

  private String getName() {
    Uri uri = this.uri;
    return uri != null ? uri.getPath() : Integer.toString(resourceId);
  }
}
