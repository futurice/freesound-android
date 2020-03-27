/*
 * Copyright 2017 Futurice GmbH
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

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Timeline

import io.reactivex.Observable
import io.reactivex.Observer

/**
 * Make an Observable from the ExoPlayer playback progress.
 *
 * Based upon techniques used in the RxBinding library.
 *
 * Note: There's no callback notification trigger only when the playback progress updates.
 * This means that consumers will need to resubscribe whenever they want to check the progress
 * updates. It's not much of an Observable!
 */
internal class ExoPlayerProgressObservable(private val exoPlayer: ExoPlayer,
                                           private val emitInitial: Boolean = true) : Observable<Long>() {

    override fun subscribeActual(observer: Observer<in Long>) {
        val listener = Listener(exoPlayer, { observer.emitCurrentTimePosition() })
        observer.onSubscribe(listener)
        exoPlayer.addListener(listener)
        if (emitInitial) {
            observer.emitCurrentTimePosition()
        }
    }

    private class Listener(exoPlayer: ExoPlayer,
                           val handleTimeChange: () -> Unit)
        : BaseExoPlayerDisposable(exoPlayer) {

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            onTimeChanged()
        }

        override fun onPositionDiscontinuity(reason: Int) {
            onTimeChanged()
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            onTimeChanged()
        }

        private fun onTimeChanged() {
            if (!isDisposed) {
                handleTimeChange()
            }
        }
    }

    private fun Observer<in Long>.emitCurrentTimePosition() = onNext(exoPlayer.currentPosition)

}
