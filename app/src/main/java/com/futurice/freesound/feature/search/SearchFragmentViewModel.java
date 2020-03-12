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

import com.futurice.freesound.arch.mvvm.SimpleViewModel;
import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.network.api.model.Sound;

import java.util.List;

import io.reactivex.Observable;
import polanski.option.Option;

import static com.futurice.freesound.common.utils.Preconditions.get;
import static com.futurice.freesound.feature.search.SearchConstants.SearchResultListItems.SOUND;

final class SearchFragmentViewModel extends SimpleViewModel {

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final AudioPlayer audioPlayer;

    @NonNull
    private SchedulerProvider schedulerProvider;

    SearchFragmentViewModel(@NonNull final SearchDataModel searchDataModel,
                            @NonNull final Navigator navigator,
                            @NonNull final AudioPlayer audioPlayer,
                            @NonNull final SchedulerProvider schedulerProvider) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
        this.audioPlayer = get(audioPlayer);
        this.schedulerProvider = schedulerProvider;
    }

    @NonNull
    Observable<Option<List<DisplayableItem<Sound>>>> getSoundsOnceAndStream() {
        // When there are none results (result == null), this won't do anything.
        return searchDataModel.getSearchStateOnceAndStream()
                .observeOn(schedulerProvider.ui())
                .map(SearchFragmentViewModel::extractResults)
                .map(it -> it.map(SearchFragmentViewModel::wrapInDisplayableItem))
                .doOnNext(__ -> audioPlayer.stopPlayback());
    }

    private static Option<List<Sound>> extractResults(KSearchState searchState) {
        // FIXME Again not the best implementation, but this class will become MVI and Kotlin.
        if (searchState instanceof KSearchState.InProgress) {
            return Option.ofObj(((KSearchState.InProgress) searchState).getSounds());
        } else if (searchState instanceof KSearchState.Success) {
            return Option.ofObj(((KSearchState.Success) searchState).getSounds());
        }
        return Option.none();
    }

    @NonNull
    Observable<KSearchState> getSearchStateOnceAndStream() {
        return searchDataModel.getSearchStateOnceAndStream();
    }

    void stopPlayback() {
        audioPlayer.stopPlayback();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @NonNull
    private static List<DisplayableItem<Sound>> wrapInDisplayableItem(
            @NonNull final List<Sound> sounds) {
        return Observable.fromIterable(sounds)
                .map(sound -> new DisplayableItem<>(sound, SOUND))
                .toList()
                .blockingGet();
    }
}
