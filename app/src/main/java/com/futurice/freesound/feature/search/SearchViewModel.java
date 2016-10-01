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
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.utils.TextUtils;
import com.futurice.freesound.viewmodel.BaseViewModel;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import polanski.option.Option;

import static com.futurice.freesound.feature.common.DisplayableItem.Type.SOUND;
import static com.futurice.freesound.functional.Functions.nothing1;
import static com.futurice.freesound.utils.Preconditions.get;
import static timber.log.Timber.e;

final class SearchViewModel extends BaseViewModel {

    private static final int SEARCH_DEBOUNCE_TIME_SECONDS = 2;

    static final String NO_SEARCH = "";

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final Analytics analytics;

    @NonNull
    private final Subject<String> searchTermOnceAndStream = BehaviorSubject
            .createDefault(NO_SEARCH);

    SearchViewModel(@NonNull final SearchDataModel searchDataModel,
                    @NonNull final Navigator navigator,
                    @NonNull final Analytics analytics) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
        this.analytics = get(analytics);
    }

    @NonNull
    Observable<Boolean> isClearButtonVisibleOnceAndStream() {
        return searchTermOnceAndStream.observeOn(Schedulers.computation())
                                      .map(SearchViewModel::isCloseEnabled);

    }

    void search(@NonNull final String query) {
        analytics.log("SearchPressedEvent");
        searchTermOnceAndStream.onNext(query);
    }

    @NonNull
    Observable<Option<List<DisplayableItem>>> getSoundsStream() {
        return searchDataModel.getSearchResultsStream()
                              .map(it -> it.map(SearchViewModel::wrapInDisplayableItem));
    }

    @NonNull
    private static List<DisplayableItem> wrapInDisplayableItem(@NonNull final List<Sound> sounds) {
        return Observable.fromIterable(sounds)
                         .map(sound -> DisplayableItem.create(sound, SOUND))
                         .toList()
                         .blockingFirst();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @Override
    public void bind(@NonNull final CompositeDisposable d) {
        d.add(searchTermOnceAndStream.observeOn(Schedulers.computation())
                                     .map(String::trim)
                                     .debounce(SEARCH_DEBOUNCE_TIME_SECONDS,
                                               TimeUnit.SECONDS)
                                     .switchMap(query -> searchOrClear(query).toObservable())
                                     .subscribe(nothing1(),
                                                e -> e(e,
                                                       "Error when setting search term")));
    }

    @NonNull
    private Completable searchOrClear(@NonNull final String searchQuery) {
        return TextUtils.isNullOrEmpty(searchQuery)
                ? searchDataModel.clear()
                : searchDataModel.querySearch(searchQuery);
    }

    private static boolean isCloseEnabled(@NonNull final String query) {
        return TextUtils.isNotNullOrEmpty(query);
    }

}
