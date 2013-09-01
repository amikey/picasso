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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.IOException;

import static android.content.ContentUris.parseId;
import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;
import static android.provider.MediaStore.Images.Thumbnails.getThumbnail;
import static com.squareup.picasso.ContentProviderBitmapHunter.PicassoKind.MICRO;
import static com.squareup.picasso.ContentProviderBitmapHunter.PicassoKind.MINI;

class ContentProviderBitmapHunter extends ContentStreamBitmapHunter {
  private static final String[] CONTENT_ORIENTATION = new String[] {
      MediaStore.Images.ImageColumns.ORIENTATION
  };

  ContentProviderBitmapHunter(Context context, Picasso picasso, Dispatcher dispatcher, Cache cache,
      Stats stats, Action action) {
    super(context, picasso, dispatcher, cache, stats, action);
  }

  @Override Bitmap decode(Request data) throws IOException {
      ContentResolver contentResolver = context.getContentResolver();
      setExifRotation(getContentProviderExifRotation(contentResolver, data.uri));

      if (data.hasSize()) {
          PicassoKind kind = getPicassoKind(data.targetWidth, data.targetHeight);
          if (kind == null) {
              Bitmap bitmap = super.decode(data);
              return bitmap;
          }

          long id = parseId(data.uri);

          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;

          calculateInSampleSize(data.targetWidth, data.targetHeight, kind.width, kind.height, options);

          return getThumbnail(contentResolver, id, kind.kind, options);
      }

      return super.decode(data);
  }

    static PicassoKind getPicassoKind(int targetWidth, int targetHeight) {
        if (targetWidth <= 96 && targetHeight <= 96) {
            return MICRO;
        } else if (targetWidth <= 512 && targetHeight <= 384) {
            return MINI;
        }
        return null;
    }

    static int getContentProviderExifRotation(ContentResolver contentResolver, Uri uri) {
    Cursor cursor = null;
    try {
      cursor = contentResolver.query(uri, CONTENT_ORIENTATION, null, null, null);
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

    enum PicassoKind {
        MICRO(MICRO_KIND, 96, 96),
        MINI(MINI_KIND, 512, 384);

        final int width;
        final int height;
        final int kind;

        PicassoKind(int kind, int width, int height) {
            this.kind = kind;
            this.width = width;
            this.height = height;
        }
    }
}
