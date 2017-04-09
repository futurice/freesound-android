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
import io.reactivex.functions.Function3;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import polanski.option.Option;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class DefaultSearchDataModel implements SearchDataModel {

    @NonNull
    private final FreeSoundApiService freeSoundApiService;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final Subject<Boolean> inProgressOnceAndStream = BehaviorSubject.createDefault(false);

    @NonNull
    private final Subject<Option<List<Sound>>> resultsOnceAndStream = BehaviorSubject.createDefault(
            Option.none());

    @NonNull
    private final Subject<Option<Throwable>> errorOnceAndStream = BehaviorSubject.createDefault(
            Option.none());

    DefaultSearchDataModel(@NonNull final FreeSoundApiService freeSoundApiService,
                           @NonNull final SchedulerProvider schedulerProvider) {
        this.freeSoundApiService = get(freeSoundApiService);
        this.schedulerProvider = get(schedulerProvider);
    }

    @NonNull
    @Override
    public Completable querySearch(@NonNull final String query,
                                   @NonNull final Completable preliminaryTask) {
        return preliminaryTask.doOnSubscribe(__ -> reportInProgress())
                              //   .doFinally(this::reportNotInProgress)
                              .andThen(freeSoundApiService.search(get(query))
                                                          .map(DefaultSearchDataModel::toResults)
                                                          .doOnSuccess(this::reportResults)
                                                          .doOnError(this::reportError)
                                                          .toCompletable()
                                                          .onErrorComplete());
    }

    @Override
    @NonNull
    public Observable<SearchState> getSearchStateOnceAndStream() {

//        return searchStateOnceAndStream.observeOn(schedulerProvider.computation())

        return Observable.combineLatest(resultsOnceAndStream, errorOnceAndStream,
                                        inProgressOnceAndStream,
                                        new Function3<Option<List<Sound>>, Option<Throwable>, Boolean, SearchState>() {
                                            @Override
                                            public SearchState apply(
                                                    @io.reactivex.annotations.NonNull final Option<List<Sound>> listOption,
                                                    @io.reactivex.annotations.NonNull final Option<Throwable> throwableOption,
                                                    @io.reactivex.annotations.NonNull final Boolean aBoolean)
                                                    throws Exception {
                                                return SearchState
                                                        .create(listOption, throwableOption,
                                                                aBoolean);
                                            }
                                        })
                         .observeOn(schedulerProvider.computation())
                         .distinctUntilChanged();
    }

    @Override
    @NonNull
    public Completable clear() {
        return Completable.fromAction(this::reportClear);
    }

    @NonNull
    private static List<Sound> toResults(@NonNull final SoundSearchResult soundSearchResult) {
        return soundSearchResult.results();
    }

    private void reportClear() {
        resultsOnceAndStream.onNext(Option.none());
        errorOnceAndStream.onNext(Option.none());
        inProgressOnceAndStream.onNext(false);
    }

    private void reportInProgress() {
        inProgressOnceAndStream.onNext(true);
    }

    private void reportResults(@NonNull final List<Sound> results) {
        resultsOnceAndStream.onNext(Option.ofObj(results));
        errorOnceAndStream.onNext(Option.none());
        inProgressOnceAndStream.onNext(false);
    }

    private void reportError(@NonNull final Throwable e) {
        errorOnceAndStream.onNext(Option.ofObj(e));
        inProgressOnceAndStream.onNext(false);
    }

}
