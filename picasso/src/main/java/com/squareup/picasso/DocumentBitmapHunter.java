// Copyright 2014 Square, Inc.
package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.IOException;

import static com.squareup.picasso.Picasso.LoadedFrom.DISK;
import static com.squareup.picasso.Utils.getExifOrientation;

class DocumentBitmapHunter extends BitmapHunter {
  final Context context;

  DocumentBitmapHunter(Context context, Picasso picasso, Dispatcher dispatcher, Cache cache,
      Stats stats, Action action) {
    super(picasso, dispatcher, cache, stats, action);
    this.context = context;
  }

  @Override Bitmap decode(Request data) throws IOException {
    setExifRotation(getExifOrientation(context, data.uri));

    ParcelFileDescriptor parcelFileDescriptor =
        context.getContentResolver().openFileDescriptor(data.uri, "r");
    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

    try {
      BitmapFactory.Options options = createBitmapOptions(data);
      if (requiresInSampleSize(options)) {
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        calculateInSampleSize(data.targetWidth, data.targetHeight, options);
      }
      return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    } finally {
      parcelFileDescriptor.close();
    }
  }

  @Override Picasso.LoadedFrom getLoadedFrom() {
    return DISK;
  }
}
