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

import android.net.Uri;
import java.util.List;

final class TargetRequest extends Request<Target> {

  TargetRequest(Picasso picasso, Uri uri, int resourceId, Target target,
      PicassoBitmapOptions bitmapOptions, List<Transformation> transformations, boolean skipCache) {
    super(picasso, uri, resourceId, target, bitmapOptions, transformations, skipCache, false, 0,
        null);
  }

  @Override void complete() {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete request with no result!\n%s", this));
    }

    Target target = this.target.get();
    if (target == null) {
      return;
    }

    target.onSuccess(result, loadedFrom);

    if (result.isRecycled()) {
      throw new IllegalStateException("Target callback must not recycle bitmap!");
    }
  }

  @Override void error() {
    Target target = getTarget();
    if (target == null) {
      return;
    }

    target.onError();
  }
}
