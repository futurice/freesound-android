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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.functional.StringFunctions;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.utils.TextUtils;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.BehaviorRelay;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import polanski.option.Option;
import polanski.option.Unit;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.futurice.freesound.functional.Functions.nothing1;
import static com.futurice.freesound.utils.Preconditions.get;

final class SearchViewModel extends BaseViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();

    static final String NO_SEARCH = "";

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final Analytics analytics;

    @NonNull
    private final BehaviorRelay<String> searchTermRelay = BehaviorRelay.create(NO_SEARCH);

    SearchViewModel(@NonNull final SearchDataModel searchDataModel,
                    @NonNull final Navigator navigator,
                    @NonNull final Analytics analytics) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
        this.analytics = get(analytics);
    }

    @NonNull
    Observable<Boolean> getClearButtonVisibleStream() {
        return searchTermRelay.asObservable()
                              .map(SearchViewModel::isCloseEnabled);

    }

    void search(@NonNull final String query) {
        analytics.log("SearchPressedEvent");
        searchTermRelay.call(query);
    }

    @NonNull
    Observable<Option<List<Sound>>> getSounds() {
        return searchDataModel.getSearchResults();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        searchTermRelay.subscribeOn(Schedulers.computation())
                       .map(String::trim)
                       .switchMap(this::searchOrClear)
                       .subscribe(nothing1(),
                                  e -> Timber.e("Error when setting search term", e));
    }

    private Observable<Unit> searchOrClear(@NonNull final String s) {
        return TextUtils.isNullOrEmpty(s) ?
                searchDataModel.clear() :
                Observable.just(s)
                          .filter(StringFunctions.isNotEmpty())
                          .debounce(2, TimeUnit.SECONDS)
                          .switchMap(searchDataModel::querySearch);
    }

    private static boolean isCloseEnabled(@NonNull final String query) {
        return TextUtils.isNotNullOrEmpty(query);
    }

}
