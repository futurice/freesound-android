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
import com.futurice.freesound.viewmodel.BaseViewModel;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import polanski.option.Unit;
import timber.log.Timber;



import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.computation;

final class HomeViewModel extends BaseViewModel {

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final PublishSubject<Unit> openSearch = PublishSubject.create();

    HomeViewModel(@NonNull final Navigator navigator) {
        this.navigator = get(navigator);
    }

    void openSearch() {
        openSearch.onNext(Unit.DEFAULT);
    }

    @Override
    public void bind(@NonNull final CompositeDisposable subscriptions) {
        openSearch.subscribeOn(mainThread())
                  .observeOn(computation())
                  .subscribe(__ -> navigator.openSearch(),
                             e -> Timber.e(e, "Error clearing search"));

    }
}
