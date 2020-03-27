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
import com.futurice.freesound.feature.audio.AudioPlayer
import com.futurice.freesound.feature.audio.PlaybackSource
import com.futurice.freesound.feature.audio.PlayerState
import com.futurice.freesound.feature.audio.from
import com.futurice.freesound.feature.common.Navigator
import com.futurice.freesound.network.api.FreeSoundApiService
import com.futurice.freesound.network.api.model.Sound
import io.reactivex.Observable
import io.reactivex.Single
import polanski.option.Option
import java.text.DateFormat

internal class SoundItemViewModel(private val sound: Sound,
                                  private val navigator: Navigator,
                                  private val audioPlayer: AudioPlayer,
                                  private val freeSoundApiService: FreeSoundApiService) : SimpleViewModel() {

    private val thumbnail: String = sound.images.medSizeWaveformUrl

    private val currentPercentage: Observable<Option<Int>>
        get() = audioPlayer.playerStateOnceAndStream
                .filter { it is PlayerState.Assigned }
                .map { it as PlayerState.Assigned }
                .map { state -> toPercentage(state.timePositionMs, sound.duration) }
                .map { Option.ofObj(it) }

    fun thumbnailImageUrl(): Single<String> = Single.just(thumbnail)

    fun name(): Single<String> = Single.just(sound.name)

    fun userAvatar(): Single<String> =
            freeSoundApiService.getUser(sound.username)
                    .map { user -> user.avatar.medium }
                    .cache()

    fun createdDate(): Single<String> =
            Single.just(sound.created)
                    .map { it.time }
                    .map { d -> DateFormat.getDateInstance().format(d) }

    fun username(): Single<String> = Single.just(sound.username)

    fun description(): Single<String> = Single.just(sound.description)

    fun duration(): Single<Int> =
            Single.just(sound.duration)
                    .map { duration -> Math.ceil(duration.toDouble()).toInt() }
                    .map { duration -> Math.max(duration, 1) }

    fun progressPercentage(): Observable<Option<Int>> =
            audioPlayer.playerStateOnceAndStream
                    .switchMap(this::progressOrNothing)

    fun openDetails() {
        navigator.openSoundDetails(sound)
    }

    fun toggleSoundPlayback() {
        audioPlayer.togglePlayback(
                PlaybackSource(from(sound.id), sound.previews.lowQualityMp3Url))
    }

    private fun progressOrNothing(playerState: PlayerState): Observable<Option<Int>> {
        return if (isThisSound(playerState)) currentPercentage else Observable.just(Option.none())
    }

    private fun isThisSound(playerState: PlayerState): Boolean =
            when (playerState) {
                PlayerState.Idle -> false
                is PlayerState.Assigned -> playerState.source.id == from(sound.id)
            }

    private fun toPercentage(positionMs: Long, durationSec: Float): Int =
            Math.min(100, (positionMs / (durationSec * 1000.0f) * 100L).toInt())

}
