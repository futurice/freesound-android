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

import com.futurice.freesound.common.Text;
import com.futurice.freesound.common.utils.TextUtils;
import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.viewmodel.BaseViewModel;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static com.futurice.freesound.common.functional.Functions.nothing1;
import static com.futurice.freesound.common.utils.Preconditions.get;
import static timber.log.Timber.e;

final class SearchActivityViewModel extends BaseViewModel {

    @VisibleForTesting
    static final int SEARCH_DEBOUNCE_TIME_SECONDS = 1;

    @VisibleForTesting
    static final String SEARCH_DEBOUNCE_TAG = "SEARCH DEBOUNCE";

    static final String NO_SEARCH = Text.EMPTY;

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final AudioPlayer audioPlayer;

    @NonNull
    private final Analytics analytics;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final Subject<String> searchTermOnceAndStream = BehaviorSubject
            .createDefault(NO_SEARCH);

    SearchActivityViewModel(@NonNull final SearchDataModel searchDataModel,
                            @NonNull final AudioPlayer audioPlayer,
                            @NonNull final Analytics analytics,
                            @NonNull final SchedulerProvider schedulerProvider) {
        this.searchDataModel = get(searchDataModel);
        this.audioPlayer = get(audioPlayer);
        this.analytics = get(analytics);
        this.schedulerProvider = get(schedulerProvider);
    }

    @Override
    protected void bind(@NonNull final CompositeDisposable d) {
        audioPlayer.init();

        d.add(searchTermOnceAndStream.observeOn(schedulerProvider.computation())
                                     .distinctUntilChanged()
                                     .switchMap(query -> TextUtils.isNotEmpty(query)
                                             ? querySearch(query).toObservable()
                                             : searchDataModel.clear().toObservable())
                                     .subscribeOn(schedulerProvider.computation())
                                     .subscribe(nothing1(),
                                                e -> e(e,
                                                       "Fatal error when setting search term")));
    }

    @Override
    protected void unbind() {
        audioPlayer.release();
    }

    void search(@NonNull final String query) {
        analytics.log("SearchPressedEvent");
        searchTermOnceAndStream.onNext(query.trim());
    }

    @NonNull
    Observable<Boolean> isClearEnabledOnceAndStream() {
        return searchTermOnceAndStream.observeOn(schedulerProvider.computation())
                                      .map(SearchActivityViewModel::isCloseEnabled);

    }

    @NonNull
    Observable<SearchState> getSearchStateOnceAndStream() {
        return searchDataModel.getSearchStateOnceAndStream()
                              .distinctUntilChanged();
    }

    @NonNull
    private Completable querySearch(@NonNull final String query) {
        return searchDataModel.querySearch(query, debounceQuery());
    }

    @NonNull
    private Completable debounceQuery() {
        return Completable.timer(SEARCH_DEBOUNCE_TIME_SECONDS,
                                 TimeUnit.SECONDS,
                                 schedulerProvider.time(SEARCH_DEBOUNCE_TAG));
    }

    private static boolean isCloseEnabled(@NonNull final String query) {
        return TextUtils.isNotNullOrEmpty(query);
    }

}
