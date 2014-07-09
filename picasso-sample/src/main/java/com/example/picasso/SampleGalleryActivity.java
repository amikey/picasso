package com.example.picasso;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewAnimator;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import static com.squareup.picasso.Callback.EmptyCallback;

public class SampleGalleryActivity extends PicassoSampleActivity {
  private static final int GALLERY_REQUEST = 9391;
  private static final String KEY_IMAGE = "com.example.picasso:image";

  private ImageView imageView;
  private ViewAnimator animator;
  private String image;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.sample_gallery_activity);

    animator = (ViewAnimator) findViewById(R.id.animator);
    imageView = (ImageView) findViewById(R.id.image);

    findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, GALLERY_REQUEST);
      }
    });

    if (savedInstanceState != null) {
      image = savedInstanceState.getString(KEY_IMAGE);
      if (image != null) {
        loadImage();
      }
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (isFinishing()) {
      // Always cancel the request here, this is safe to call even if the image has been loaded.
      // This ensures that the anonymous callback we have does not prevent the activity from
      // being garbage collected. It also prevents our callback from getting invoked even after the
      // activity has finished.
      Picasso.with(this).cancelRequest(imageView);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_IMAGE, image);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
      image = data.getData().toString();
      loadImage();
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void loadImage() {
    // Index 1 is the progress bar. Show it while we're loading the image.
    animator.setDisplayedChild(1);

    Uri uri = Uri.parse(image);
    Picasso.with(this)
        .load(uri)
        .fit()
        .skipMemoryCache()
        .transform(new DocumentExifTransformation(this, uri))
        .into(imageView, new EmptyCallback() {
          @Override public void onSuccess() {
            // Index 0 is the image view.
            animator.setDisplayedChild(0);
          }
        });
  }

  static class DocumentExifTransformation implements Transformation {
    private static final String[] CONTENT_ORIENTATION = new String[] {
        MediaStore.Images.ImageColumns.ORIENTATION
    };

    final Context context;
    final Uri uri;

    DocumentExifTransformation(Context context, Uri uri) {
      this.context = context;
      this.uri = uri;
    }

    @Override public Bitmap transform(Bitmap source) {
      if (!DocumentsContract.isDocumentUri(context, uri)) return source;

      int exifRotation = getExifOrientation(context, uri);
      if (exifRotation != 0) {
        Matrix matrix = new Matrix();
        matrix.preRotate(exifRotation);

        Bitmap rotated =
            Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        if (rotated != source) {
          source.recycle();
        }
        return rotated;
      }

      return source;
    }

    @Override public String key() {
      return "documentExifTransform(" + DocumentsContract.getDocumentId(uri) + ")";
    }

    static int getExifOrientation(Context context, Uri uri) {
      ContentResolver contentResolver = context.getContentResolver();
      Cursor cursor = null;
      try {
        String id = DocumentsContract.getDocumentId(uri);
        id = id.split(":")[1];
        cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            CONTENT_ORIENTATION, MediaStore.Images.Media._ID + " = ?", new String[] { id }, null);
        if (cursor == null || !cursor.moveToFirst()) {
          return 0;
        }
        return cursor.getInt(0);
      } catch (RuntimeException ignored) {
        // If the orientation column doesn't exist, assume no rotation.
        return 0;
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }
    }
  }
}
