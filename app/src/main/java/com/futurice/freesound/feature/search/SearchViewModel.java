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

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;
import polanski.option.Unit;
import timber.log.Timber;

import static com.futurice.freesound.functional.Functions.nothing1;
import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

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
    private final BehaviorSubject<String> searchTermRelay = BehaviorSubject
            .createDefault(NO_SEARCH);

    SearchViewModel(@NonNull final SearchDataModel searchDataModel,
                    @NonNull final Navigator navigator,
                    @NonNull final Analytics analytics) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
        this.analytics = get(analytics);
    }

    @NonNull
    Observable<Boolean> getClearButtonVisibleStream() {
        return searchTermRelay.map(SearchViewModel::isCloseEnabled);

    }

    void search(@NonNull final String query) {
        analytics.log("SearchPressedEvent");
        searchTermRelay.onNext(query);
    }

    @NonNull
    Observable<Option<List<Sound>>> getSounds() {
        return searchDataModel.getSearchResults();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @Override
    public void bind(@NonNull final CompositeDisposable subscriptions) {
        searchTermRelay.subscribeOn(Schedulers.computation())
                       .observeOn(Schedulers.computation())
                       .map(String::trim)
                       .debounce(2, TimeUnit.SECONDS)
                       .switchMap(it -> searchOrClear(it).toObservable())
                       .observeOn(mainThread())
                       .subscribe(nothing1(),
                                  e -> Timber.e(e, "Error when setting search term"));
    }

    private Maybe<Unit> searchOrClear(@NonNull final String s) {
        return TextUtils.isNullOrEmpty(s) ?
                searchDataModel.clear().toMaybe() :
                Single.just(s)
                      .filter(StringFunctions.isNotEmpty())
                      .flatMap(it -> searchDataModel.querySearch(it).toMaybe());
    }

    private static boolean isCloseEnabled(@NonNull final String query) {
        return TextUtils.isNotNullOrEmpty(query);
    }

}
