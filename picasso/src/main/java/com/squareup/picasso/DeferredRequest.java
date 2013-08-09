package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import java.lang.ref.WeakReference;

import static com.squareup.picasso.Picasso.LoadedFrom.MEMORY;
import static com.squareup.picasso.Utils.createKey;

class DeferredRequest implements ViewTreeObserver.OnGlobalLayoutListener {
  private final Picasso picasso;
  private final Request.Builder data;
  private final boolean skipMemoryCache;
  private final boolean noFade;
  private final int placeholderResId;
  private final Drawable placeholderDrawable;
  private final int errorResId;
  private final Drawable errorDrawable;
  private final WeakReference<ImageView> target;
  private final Callback callback;

  DeferredRequest(Picasso picasso, Request.Builder data, ImageView target,
      boolean skipMemoryCache, boolean noFade, int placeholderResId, Drawable placeholderDrawable,
      int errorResId, Drawable errorDrawable, Callback callback) {
    this.picasso = picasso;
    this.data = data;
    this.skipMemoryCache = skipMemoryCache;
    this.noFade = noFade;
    this.placeholderResId = placeholderResId;
    this.placeholderDrawable = placeholderDrawable;
    this.errorResId = errorResId;
    this.errorDrawable = errorDrawable;
    this.target = new WeakReference<ImageView>(target);
    this.callback = callback;

    target.getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  void cancel() {
    ImageView target = this.target.get();
    if (target == null) {
      return;
    }
    ViewTreeObserver vto = target.getViewTreeObserver();
    if (vto.isAlive()) {
      vto.removeOnGlobalLayoutListener(this);
    }
  }

  @Override public void onGlobalLayout() {
    ImageView target = this.target.get();
    if (target == null) {
      return;
    }
    ViewTreeObserver vto = target.getViewTreeObserver();
    if (vto.isAlive()) {
      vto.removeOnGlobalLayoutListener(this);
    }

    data.resize(target.getMeasuredWidth(), target.getMeasuredHeight());

    Request finalData = picasso.transformRequest(data.build());
    String requestKey = createKey(finalData);

    if (!skipMemoryCache) {
      Bitmap bitmap = picasso.quickMemoryCacheCheck(requestKey);
      if (bitmap != null) {
        picasso.cancelRequest(target);
        PicassoDrawable.setBitmap(target, picasso.context, bitmap, MEMORY, noFade,
            picasso.debugging);
        if (callback != null) {
          callback.onSuccess();
        }
        return;
      }
    }

    PicassoDrawable.setPlaceholder(target, placeholderResId, placeholderDrawable);

    Action action = new ImageViewAction(picasso, target, finalData,
        skipMemoryCache, noFade, errorResId, errorDrawable, requestKey, callback);

    picasso.enqueueAndSubmit(action);
  }
}
