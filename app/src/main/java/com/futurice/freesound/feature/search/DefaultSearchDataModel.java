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
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.get;

final class DefaultSearchDataModel implements SearchDataModel {

    @NonNull
    private final FreeSoundSearchService freeSoundSearchService;

    @NonNull
    private final BehaviorSubject<Option<List<Sound>>> lastResultsStream = BehaviorSubject.create();

    DefaultSearchDataModel(@NonNull final FreeSoundSearchService freeSoundSearchService) {
        this.freeSoundSearchService = get(freeSoundSearchService);
    }

    @Override
    @NonNull
    public Completable querySearch(@NonNull final String query) {
        return freeSoundSearchService.search(get(query))
                                     .map(SoundSearchResult::results)
                                     .map(Option::ofObj)
                                     .doOnSuccess(lastResultsStream::onNext)
                                     .toCompletable();
    }

    @Override
    @NonNull
    public Observable<Option<List<Sound>>> getSearchResultsStream() {
        return lastResultsStream.hide();
    }

    @Override
    @NonNull
    public Completable clear() {
        return Completable.fromAction(() -> lastResultsStream.onNext(Option.none()));
    }
}
