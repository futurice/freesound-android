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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import static com.futurice.freesound.common.utils.Preconditions.get;
import static timber.log.Timber.e;

final class DefaultSearchDataModel implements SearchDataModel {

    @NonNull
    private final FreeSoundApiService freeSoundApiService;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final Subject<SearchState> searchStateOnceAndStream =
            BehaviorSubject.createDefault(SearchState.idle());

    DefaultSearchDataModel(@NonNull final FreeSoundApiService freeSoundApiService,
                           @NonNull final SchedulerProvider schedulerProvider) {
        this.freeSoundApiService = get(freeSoundApiService);
        this.schedulerProvider = get(schedulerProvider);
    }

    @NonNull
    @Override
    public Completable querySearch(@NonNull final String query,
                                   @NonNull final Completable afterThis) {
        return afterThis.doOnSubscribe(__ -> reportInProgress())
                        .andThen(freeSoundApiService.search(get(query))
                                                    .map(DefaultSearchDataModel::toResults)
                                                    .doOnSuccess(this::storeValueAndClearError)
                                                    .doOnError(storeError(query))
                                                    .toCompletable()
                                                    .onErrorComplete());
    }

    @Override
    @NonNull
    public Observable<SearchState> getSearchStateOnceAndStream() {
        return searchStateOnceAndStream.observeOn(schedulerProvider.computation());
    }

    @Override
    @NonNull
    public Completable clear() {
        return Completable.fromAction(this::clearResultAndError);
    }

    @NonNull
    private static List<Sound> toResults(@NonNull final SoundSearchResult soundSearchResult) {
        return soundSearchResult.results();
    }

    private void reportInProgress() {
        searchStateOnceAndStream.onNext(SearchState.inProgress());
    }

    private void storeValueAndClearError(@NonNull final List<Sound> results) {
        searchStateOnceAndStream.onNext(SearchState.success(results));
    }

    @NonNull
    private Consumer<Throwable> storeError(@NonNull final String query) {
        return e -> {
            searchStateOnceAndStream.onNext(SearchState.error(e));
            e(e, "Error searching Freesound for query: %s ", query);
        };
    }

    private void clearResultAndError() {
        searchStateOnceAndStream.onNext(SearchState.idle());
    }
}
