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

package com.futurice.freesound.feature.audio

import com.futurice.freesound.common.Releasable

import io.reactivex.Observable

/**
 * An AudioPlayer for URL based sources.
 */
interface AudioPlayer : Releasable {

    /**
     * Reports the initial player status and subsequent changes.
     *
     * @return A stream of the [PlayerState].
     */
    val playerStateOnceAndStream: Observable<out PlayerState>

    /**
     * Initialize the player.
     */
    fun init()

    /**
     * Toggles the playback for the given URL.
     *
     * Starts playback if currently playing, otherwise will pause.
     *
     * @param playbackSource the audio source.
     */
    fun togglePlayback(playbackSource: PlaybackSource)

    /**
     * Stops the current playback and reset state.
     */
    fun stopPlayback()

    /**
     * Dispose of the player, the instance cannot be reused.
     */
    override fun release()
}
