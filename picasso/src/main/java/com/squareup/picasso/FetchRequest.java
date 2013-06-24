package com.squareup.picasso;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.util.List;

class FetchRequest extends Request<Void> {

  FetchRequest(Picasso picasso, Uri uri, int resourceId, PicassoBitmapOptions options,
      List<Transformation> transformations, boolean skipCache, boolean noFade, int errorResId,
      Drawable errorDrawable) {
    super(picasso, uri, resourceId, null, options, transformations, skipCache, noFade, errorResId,
        errorDrawable);
  }

  @Override public void complete() {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete request with no result!\n%s", this));
    }
  }

  @Override public void error() {
    // Failure callback
  }

  @Override public void retry() {
    if (retryCount > 0) {
      retryCount--;
      picasso.submit(this);
    } else {
      error();
    }
  }
}
