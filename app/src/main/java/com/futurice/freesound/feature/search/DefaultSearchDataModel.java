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

import androidx.annotation.NonNull;

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import polanski.option.Option;
import polanski.option.OptionUnsafe;

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
    public Observable<KSearchState> getSearchStateOnceAndStream() {
        return Observable.combineLatest(resultsOnceAndStream,
                errorOnceAndStream,
                inProgressOnceAndStream,
                DefaultSearchDataModel::combine)
                .observeOn(schedulerProvider.computation())
                .distinctUntilChanged();
    }

    private static KSearchState combine(Option<List<Sound>> results, Option<Throwable> error, Boolean inProgress) {
        // FIXME This is not an ideal implementation, but:
        //  1. This ViewModel will be re-implemented as MVI
        //  2. I really want to get rid of AutoValue and this is the last usage of it.

        if (error.isSome()) {
            // No combining errors and existing state, sorry.
            return new KSearchState.Error(OptionUnsafe.getUnsafe(error));
        }

        if (inProgress) {
            // Progress can exist with results
            return new KSearchState.InProgress(results.orDefault(() -> null));
        }

        if (results.isSome()) {
            // No error and not in progress, but have results -> success!
            return new KSearchState.Success(OptionUnsafe.getUnsafe(results));
        }

        // Nothingness.
        return KSearchState.Cleared.INSTANCE;
    }

    @Override
    @NonNull
    public Completable clear() {
        return Completable.fromAction(this::reportClear);
    }

    @NonNull
    private static List<Sound> toResults(@NonNull final SoundSearchResult soundSearchResult) {
        return soundSearchResult.getResults();
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
