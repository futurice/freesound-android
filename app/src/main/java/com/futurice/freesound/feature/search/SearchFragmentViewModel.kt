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
package com.futurice.freesound.feature.search

import com.futurice.freesound.arch.mvvm.SimpleViewModel
import com.futurice.freesound.common.utils.Preconditions
import com.futurice.freesound.feature.audio.AudioPlayer
import com.futurice.freesound.feature.common.DisplayableItem
import com.futurice.freesound.feature.common.Navigator
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.feature.search.SearchResultListItems.SOUND
import com.futurice.freesound.network.api.model.Sound
import io.reactivex.Observable
import polanski.option.Option

internal class SearchFragmentViewModel(private val searchDataModel: SearchDataModel,
                                       private val navigator: Navigator,
                                       private val audioPlayer: AudioPlayer,
                                       private val schedulerProvider: SchedulerProvider) : SimpleViewModel() {

    // When there are none results (result == null), this won't do anything.
    val soundsOnceAndStream
        get() = searchDataModel.searchStateOnceAndStream
                .observeOn(schedulerProvider.ui())
                .map { searchState: SearchState -> extractResults(searchState) }
                .map { it.map { sounds -> wrapInDisplayableItem(sounds) } }
                .doOnNext { audioPlayer.stopPlayback() }

    val searchStateOnceAndStream: Observable<SearchState>
        get() = searchDataModel.searchStateOnceAndStream

    fun stopPlayback() = audioPlayer.stopPlayback()

    fun openSoundDetails(sound: Sound) {
        navigator.openSoundDetails(Preconditions.get(sound))
    }

    private fun extractResults(searchState: SearchState): Option<List<Sound>> {
        // FIXME Again not the best implementation, but this class will become MVI and Kotlin.
        if (searchState is SearchState.InProgress) {
            return Option.ofObj(searchState.sounds)
        } else if (searchState is SearchState.Success) {
            return Option.ofObj(searchState.sounds)
        }
        return Option.none()
    }

    private fun wrapInDisplayableItem(sounds: List<Sound>): List<DisplayableItem<Sound>> {
        return Observable.fromIterable(sounds)
                .map { DisplayableItem(it, SOUND) }
                .toList()
                .blockingGet()
    }

}