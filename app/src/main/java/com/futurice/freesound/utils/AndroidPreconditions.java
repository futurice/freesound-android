/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    private AndroidPreconditions() {
        throw new AssertionError("No instances allowed");
    }

}
