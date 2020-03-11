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

package com.futurice.freesound.common.rx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Scheduler;
import io.reactivex.functions.BiFunction;

import static com.futurice.freesound.common.functional.Functions.apply;
import static io.reactivex.schedulers.Schedulers.computation;

/**
 * Scheduler used for time based operations.
 *
 * During normal application runtime, the computation scheduler should be used.
 * But during the tests, there is possibility to override it to be able to better
 * test time based actions.
 */
public final class TimeScheduler {

    @Nullable
    private static volatile BiFunction<Scheduler, String, Scheduler> onTimeHandler = null;

    /**
     * Returns scheduler for time operations.
     *
     * @param tag Tag to be used in tests to mock the scheduler
     */
    public static Scheduler time(@NonNull final String tag) {
        BiFunction<Scheduler, String, Scheduler> f = onTimeHandler;
        if (f == null) {
            return computation();
        }
        return apply(f, computation(), tag);
    }

    /**
     * Sets handler for time scheduler.
     *
     * @param handler Handler to be used
     */
    public static void setTimeSchedulerHandler(
            @Nullable final BiFunction<Scheduler, String, Scheduler> handler) {
        onTimeHandler = handler;
    }

    /**
     * Resets current scheduler handler.
     */
    public static void reset() {
        onTimeHandler = null;
    }
}
