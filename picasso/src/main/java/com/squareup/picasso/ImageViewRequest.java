package com.squareup.picasso;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import java.util.List;

class ImageViewRequest extends Request<ImageView> {

  private Callback callback;

  ImageViewRequest(Picasso picasso, Uri uri, int resourceId, ImageView imageView,
      PicassoBitmapOptions options, List<Transformation> transformations, boolean skipCache,
      boolean noFade, int errorResId, Drawable errorDrawable, Callback callback) {
    super(picasso, uri, resourceId, imageView, options, transformations, skipCache, noFade,
        errorResId, errorDrawable);
    this.callback = callback;
  }

  @Override public void complete() {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete request with no result!\n%s", this));
    }

    ImageView target = this.target.get();
    if (target == null) {
      return;
    }

    Context context = picasso.context;
    boolean debugging = picasso.debugging;
    PicassoDrawable.setBitmap(target, context, result, loadedFrom, noFade, debugging);

    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override public void error() {
    ImageView target = this.target.get();
    if (target == null) {
      return;
    }
    if (errorResId != 0) {
      target.setImageResource(errorResId);
    } else if (errorDrawable != null) {
      target.setImageDrawable(errorDrawable);
    }

    if (callback != null) {
      callback.onError();
    }
  }

  @Override void cancel(Uri uri) {
    super.cancel(uri);
    callback = null;
  }
}

