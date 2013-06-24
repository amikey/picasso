package com.squareup.picasso;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.util.List;

public class TestRequest extends Request<Void> {

  TestRequest(Picasso picasso, Uri uri, int resourceId, Void target, PicassoBitmapOptions options,
      List<Transformation> transformations, boolean skipCache, boolean noFade, int errorResId,
      Drawable errorDrawable, Callback callback) {
    super(picasso, uri, resourceId, target, options, transformations, skipCache, noFade, errorResId,
        errorDrawable);
  }

  @Override void complete() {
  }

  @Override void error() {
  }
}
