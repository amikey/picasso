package com.squareup.picasso;

import android.graphics.Bitmap;

/**
 * Represents an arbitrary listener for image loading.
 * <p>
 * Objects implementing this class <strong>must</strong> have a working implementation of
 * {@link #equals(Object)} and {@link #hashCode()} for proper storage internally. Instances of this
 * interface will also be compared to determine if view recycling is occurring. It is recommended
 * that you implement this interface directly on a view type when using in an adapter to ensure
 * correct recycling behavior.
 * <p>
 * <pre>
 * public class UserView extends FrameLayout implements Target {
 *   public UserView(Context context, AttributeSet attrs) {
 *     super(context, attrs);
 *     setBackgroundResource(R.drawable.user_image_placeholder);
 *   }
 *
 *   &#64;Override public void onImageLoaded(Bitmap bitmap) {
 *     setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
 *   }
 *
 *   &#64;Override public void onImageLoadFailed() {
 *     setBackgroundResource(R.drawable.user_image_error);
 *   }
 * }
 * // ...
 * UserView userView = (UserView) findViewById(R.id.user_view);
 * Picasso.with(context).load(url).into(userView);
 * </pre>
 */
public interface Target {
  /**
   * Callback when an image has been successfully loaded.
   * <p/>
   * <strong>Note:</strong> You must not recycle the bitmap.
   */
  void onImageLoaded(Bitmap bitmap);

  /** Callback indicating the image could not be successfully loaded. */
  void onImageLoadFailed();
}
