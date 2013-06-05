package com.squareup.picasso;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The default {@link java.util.concurrent.ExecutorService} used for new {@link Picasso} instances.
 * <p/>
 * Exists as a custom type so that we can differentiate the use of defaults versus a user-supplied
 * instance.
 */
class PicassoExecutorService extends ThreadPoolExecutor {
  private static final int THREAD_COUNT_HIGH = 3;
  private static final int THREAD_COUNT_LOW = 1;

  PicassoExecutorService() {
    super(THREAD_COUNT_HIGH, THREAD_COUNT_HIGH, 0, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(), new Utils.PicassoThreadFactory());
  }

  void useHighThreadCount() {
    setCorePoolSize(THREAD_COUNT_HIGH);
    setMaximumPoolSize(THREAD_COUNT_HIGH);
  }

  void useLowThreadCount() {
    setCorePoolSize(THREAD_COUNT_LOW);
    setMaximumPoolSize(THREAD_COUNT_LOW);
  }
}
