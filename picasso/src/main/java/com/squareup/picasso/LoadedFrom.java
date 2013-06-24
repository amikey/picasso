package com.squareup.picasso;

import android.graphics.Color;

public enum LoadedFrom {
  MEMORY(Color.GREEN),
  DISK(Color.YELLOW),
  NETWORK(Color.RED);

  final int debugColor;

  LoadedFrom(int debugColor) {
    this.debugColor = debugColor;
  }
}
