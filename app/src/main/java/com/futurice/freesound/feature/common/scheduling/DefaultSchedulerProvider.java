/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.feature.common.scheduling;

import com.futurice.freesound.common.rx.TimeScheduler;
import com.futurice.freesound.common.utils.AndroidPreconditions;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

final class DefaultSchedulerProvider implements SchedulerProvider {

    @NonNull
    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @NonNull
    @Override
    public Scheduler trampoline() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler single() {
        return Schedulers.single();
    }

    @NonNull
    @Override
    public Scheduler newThread() {
        return Schedulers.newThread();
    }

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    @Override
    public Scheduler time(@NonNull final String tag) {
        return TimeScheduler.time(tag);
    }

    @NonNull
    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public boolean isUiThread() {
        return AndroidPreconditions.isMainThread();
    }
}
