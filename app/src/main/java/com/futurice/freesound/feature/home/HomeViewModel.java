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
import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.PublishRelay;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

final class HomeViewModel extends BaseViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final PublishRelay<Unit> openSearch = PublishRelay.create();

    HomeViewModel(@NonNull final Navigator navigator) {
        this.navigator = get(navigator);
    }

    void openSearch() {
        openSearch.call(Unit.DEFAULT);
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        openSearch.subscribeOn(AndroidSchedulers.mainThread())
                  .observeOn(Schedulers.computation())
                  .subscribe(__ -> navigator.openSearch(),
                             e -> Log.e(TAG, "Error clearing search", e));

    }
}
