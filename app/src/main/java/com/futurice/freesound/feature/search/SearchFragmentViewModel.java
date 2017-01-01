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

import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.viewmodel.BaseViewModel;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import polanski.option.Option;

import static com.futurice.freesound.feature.common.DisplayableItem.Type.SOUND;
import static com.futurice.freesound.utils.Preconditions.get;

final class SearchFragmentViewModel extends BaseViewModel {

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final AudioPlayer audioPlayer;

    SearchFragmentViewModel(@NonNull final SearchDataModel searchDataModel,
                            @NonNull final Navigator navigator,
                            @NonNull final AudioPlayer audioPlayer) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
        this.audioPlayer = get(audioPlayer);
    }

    @NonNull
    Observable<Option<List<DisplayableItem>>> getSoundsOnceAndStream() {
        return searchDataModel.getSearchResultsOnceAndStream()
                              .doOnNext(__ -> audioPlayer.stop())
                              .map(it -> it.map(SearchFragmentViewModel::wrapInDisplayableItem));
    }

    @NonNull
    private static List<DisplayableItem> wrapInDisplayableItem(@NonNull final List<Sound> sounds) {
        return Observable.fromIterable(sounds)
                         .map(sound -> DisplayableItem.create(sound, SOUND))
                         .toList()
                         .blockingGet();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @Override
    public void bind(@NonNull final CompositeDisposable d) {
        // Nothing
    }

}
