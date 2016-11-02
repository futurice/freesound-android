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

import com.futurice.freesound.network.api.FreeSoundSearchService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.get;
import static timber.log.Timber.e;

final class DefaultSearchDataModel implements SearchDataModel {

    @NonNull
    private final FreeSoundSearchService freeSoundSearchService;

    @NonNull
    private final BehaviorSubject<Option<List<Sound>>> lastResultsOnceAndStream =
            BehaviorSubject.createDefault(Option.none());

    @NonNull
    private final BehaviorSubject<Option<Throwable>> lastErrorOnceAndStream =
            BehaviorSubject.createDefault(Option.none());

    DefaultSearchDataModel(@NonNull final FreeSoundSearchService freeSoundSearchService) {
        this.freeSoundSearchService = get(freeSoundSearchService);
    }

    @Override
    public Completable querySearch(@NonNull final String query) {
        return freeSoundSearchService.search(get(query))
                                     .map(DefaultSearchDataModel::toResults)
                                     .doOnSuccess(this::storeValueAndClearError)
                                     .doOnError(storeError(query))
                                     .toCompletable()
                                     .onErrorComplete();
    }

    @Override
    @NonNull
    public Observable<Option<List<Sound>>> getSearchResultsOnceAndStream() {
        return lastResultsOnceAndStream.hide();
    }

    @Override
    @NonNull
    public Observable<Option<Throwable>> getSearchErrorOnceAndStream() {
        return lastErrorOnceAndStream.hide();
    }

    @Override
    @NonNull
    public Completable clear() {
        return Completable.fromAction(this::clearResultAndError);
    }

    @NonNull
    private static Option<List<Sound>> toResults(
            @NonNull final SoundSearchResult soundSearchResult) {
        return Option.ofObj(soundSearchResult.results());
    }

    private void storeValueAndClearError(@NonNull final Option<List<Sound>> listOption) {
        lastResultsOnceAndStream.onNext(listOption);
        lastErrorOnceAndStream.onNext(Option.none());
    }

    @NonNull
    private Consumer<Throwable> storeError(@NonNull final String query) {
        return e -> {
            lastErrorOnceAndStream.onNext(Option.ofObj(e));
            e(e, "Error searching Freesound for query: %s ", query);
        };
    }

    private void clearResultAndError() {
        lastResultsOnceAndStream.onNext(Option.none());
        lastErrorOnceAndStream.onNext(Option.none());
    }
}
