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

package com.futurice.freesound.feature.home;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import polanski.option.Unit;
import timber.log.Timber;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class HomeViewModel extends SimpleViewModel {

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final PublishSubject<Unit> openSearchStream = PublishSubject.create();

    HomeViewModel(@NonNull final Navigator navigator,
                  @NonNull final SchedulerProvider schedulerProvider) {
        this.navigator = get(navigator);
        this.schedulerProvider = get(schedulerProvider);
    }

    void openSearch() {
        openSearchStream.onNext(Unit.DEFAULT);
    }

    @Override
    protected void bind(@NonNull final CompositeDisposable disposables) {
        disposables.add(openSearchStream.observeOn(schedulerProvider.ui())
                                        .subscribe(__ -> navigator.openSearch(),
                                                   e -> Timber.e(e, "Error clearing search")));

    }
}
