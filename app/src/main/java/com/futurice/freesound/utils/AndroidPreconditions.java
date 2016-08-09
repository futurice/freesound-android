package com.futurice.freesound.utils;

import android.os.Looper;

import java.util.Objects;

/**
 * Static class that provides helper methods to check Android related preconditions.
 */
public final class AndroidPreconditions {

    /**
     * Asserts that the current thread is a worker thread.
     */
    public static void assertWorkerThread() {
        if (isMainThread()) {
            throw new IllegalStateException(
                    "This task must be run on a worker thread and not on the Main thread.");
        }
    }

    /**
     * Asserts that the current thread is the Main Thread.
     */
    public static void assertUiThread() {
        if (!isMainThread()) {
            throw new IllegalStateException(
                    "This task must be run on the Main thread and not on a worker thread.");
        }
    }

    private static boolean isMainThread() {
        return Objects.equals(Looper.getMainLooper(), Looper.myLooper());
    }

}
