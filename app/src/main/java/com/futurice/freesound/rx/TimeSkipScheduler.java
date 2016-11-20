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

package com.futurice.freesound.rx;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

/**
 * Scheduler that executes immediately even the time based actions.
 *
 * When this scheduler is used in tests with an operator where we specify time,
 * it will ignore the time values and schedule the action immediately.
 *
 * The reason behind it is to run tests as quickly as possible and not use
 * {@link io.reactivex.schedulers.TestScheduler} every time.
 */
public final class TimeSkipScheduler extends Scheduler {

    @NonNull
    private static final TimeSkipScheduler INSTANCE = new TimeSkipScheduler();

    private TimeSkipScheduler() {
    }

    /**
     * Returns the instance of time skip scheduler.
     */
    @NonNull
    public static TimeSkipScheduler instance() {
        return INSTANCE;
    }

    @Override
    public Worker createWorker() {
        return new InnerTimeSkipScheduler();
    }

    private static class InnerTimeSkipScheduler extends Worker {

        @Override
        public Disposable schedule(final Runnable run, final long delay, final TimeUnit unit) {
            run.run();

            return Disposables.empty();
        }

        @Override
        public void dispose() {
            // Empty on purpose
        }

        @Override
        public boolean isDisposed() {
            return false;
        }
    }
}
