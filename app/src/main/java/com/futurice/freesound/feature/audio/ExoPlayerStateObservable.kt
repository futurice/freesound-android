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

import com.google.android.exoplayer2.ExoPlayer
import io.reactivex.Observable
import io.reactivex.Observer

/**
 * Make an Observable from the ExoPlayer player status.
 *
 * Based upon techniques used in the RxBinding library.
 */
internal class ExoPlayerStateObservable(private val exoPlayer: ExoPlayer,
                                        private val emitInitial: Boolean = true) : Observable<ExoPlayerState>() {

    override fun subscribeActual(observer: Observer<in ExoPlayerState>) {
        val listener = Listener(exoPlayer, observer)
        observer.onSubscribe(listener)
        exoPlayer.addListener(listener)
        if (emitInitial) {
            observer.onNext(ExoPlayerState(exoPlayer.playWhenReady, exoPlayer.playbackState))
        }
    }

    private class Listener(exoPlayer: ExoPlayer,
                           val observer: Observer<in ExoPlayerState>)
        : BaseExoPlayerDisposable(exoPlayer) {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            // Strictly speaking, this check is not required because the listener is removed
            // upon disposal, therefore ExoPlayer won't keep it around to notify of changes.
            if (!isDisposed) {
                observer.onNext(ExoPlayerState(playWhenReady, playbackState))
            }
        }

    }

}
