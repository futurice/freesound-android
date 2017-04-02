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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import polanski.option.Unit;

/***
 * This implementation reports the search as being in progress before the debounce period has
 * completed. This gives the user the feedback that their input is being processed, without
 * prematurely querying the API.
 *
 * Proxies through to the {@link DefaultSearchDataModel} to perform the search.
 */
final class TypingSearchDataModel implements SearchDataModel {

    @VisibleForTesting
    static final int SEARCH_DEBOUNCE_TIME_SECONDS = 2;

    @VisibleForTesting
    static final String SEARCH_DEBOUNCE_TAG = "SEARCH DEBOUNCE";

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final PublishSubject<Unit> typeStream = PublishSubject.create();

    public TypingSearchDataModel(@NonNull final SearchDataModel searchDataModel,
                                 @NonNull final SchedulerProvider schedulerProvider) {
        this.searchDataModel = searchDataModel;
        this.schedulerProvider = schedulerProvider;
    }

    @NonNull
    @Override
    public Completable querySearch(@NonNull final String query) {
        return Observable.timer(SEARCH_DEBOUNCE_TIME_SECONDS,
                                TimeUnit.SECONDS,
                                schedulerProvider.time(SEARCH_DEBOUNCE_TAG))
                         .doOnSubscribe(__ -> typeStream.onNext(Unit.DEFAULT))
                         .flatMapCompletable(__ -> searchDataModel.querySearch(query));
    }

    @NonNull
    @Override
    public Observable<SearchState> getSearchStateOnceAndStream() {
        return Observable.merge(
                typeStream.map(__ -> SearchState.inProgress()),
                searchDataModel.getSearchStateOnceAndStream());
    }

    @NonNull
    @Override
    public Completable clear() {
        return searchDataModel.clear();
    }

}
