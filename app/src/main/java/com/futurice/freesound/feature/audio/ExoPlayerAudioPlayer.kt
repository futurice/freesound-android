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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.google.android.exoplayer2.Player
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * AudioPlayer implementation that uses ExoPlayer 2.
 *
 * This is not thread safe; you should only issue commands from the initialization thread.
 *
 * ExoPlayer documentation recommends that the player instance is only interacted with from
 * a single thread. Callbacks are provided on the same thread that initialized the ExoPlayer
 * instance.
 *
 * NOTE: I haven't yet found a way to determine the current playing source in ExoPlayer, so this
 * class needs to retain the URL itself. As a consequence, keep this instance with the same scope
 * as the underlying ExoPlayer instance. Otherwise you could arrive at a situation where ExoPlayer
 * is playing a source and this instance has no current URL defined.
 *
 * From what I can still see, this explanation to keep your own URI is still recommended:
 *  https://github.com/google/ExoPlayer/issues/2328
 */
internal class ExoPlayerAudioPlayer(private val exoPlayer: ObservableExoPlayer,
                                    private val updatePeriod: Long,
                                    private val timeUnit: TimeUnit,
                                    private val schedulerProvider: SchedulerProvider) : AudioPlayer {

    companion object {
        private val PLAYER_PROGRESS_SCHEDULER_TAG = "PLAYER_PROGRESS_SCHEDULER"
    }

    private val playbackSourceRequestDisposable = SerialDisposable()
    private val playbackSourceRequestStream = PublishSubject.create<PlaybackSource>()
    private var currentPlaybackSource: PlaybackSource? = null

    override val playerStateOnceAndStream: Observable<PlayerState>
        get() = definePlayerStateObservable()

    private fun definePlayerStateObservable(): Observable<PlayerState> {
        return exoPlayer.stateOnceAndStream
                .doOnNext { Timber.v("ExoPlayer State changed: $it") }
                .switchMap { exoPlayerState ->
                    if (exoPlayerState.playbackState == Player.STATE_IDLE)
                        Observable.just(PlayerState.Idle)
                    else
                        definePlayerTimePositionStream()
                                .map { positionMs ->
                                    toPlayerState(currentPlaybackSource, exoPlayerState.toPlaybackStatus(), positionMs)
                                }
                }
    }

    private fun toPlayerState(source: PlaybackSource?,
                              status: PlaybackStatus,
                              positionMs: Long
    ): PlayerState {
        requireNotNull(source, { "Cannot created an Assigned state without a playback source" })
        return PlayerState.Assigned(source, status, positionMs)
    }

    override fun init() {
        playbackSourceRequestDisposable
                .set(playbackSourceRequestStream
                        .doOnNext { Timber.v("New PlaybackSource Request: $it") }
                        .switchMap { newPlaybackSource ->
                            exoPlayer.stateOnceAndStream
                                    .take(1)
                                    .map { exoPlayerState -> toPlaybackRequest(currentPlaybackSource, newPlaybackSource, exoPlayerState) }
                                    .map { request -> Pair(request, newPlaybackSource) }
                        }
                        .subscribe({ pair -> handlePlaybackRequest(pair.first, pair.second) },
                                { e -> Timber.e(e, "Fatal error when changing playback source") }))
    }

    override fun togglePlayback(playbackSource: PlaybackSource) {
        playbackSourceRequestStream.onNext(playbackSource)
    }

    override fun stopPlayback() {
        exoPlayer.stop()
        currentPlaybackSource = null // Stop makes ExoPlayer Idle, so we need to unset the source
    }

    override fun release() {
        playbackSourceRequestDisposable.dispose()
        exoPlayer.release()
    }

    private fun toPlaybackRequest(current: PlaybackSource?,
                                  requested: PlaybackSource,
                                  exoPlayerState: ExoPlayerState): PlaybackRequest {

        return if (exoPlayerState.playbackState == Player.STATE_IDLE) {
            PlaybackRequest.PLAY
        } else {
            val playbackStatus: PlaybackStatus = exoPlayerState.toPlaybackStatus();
            if ((requested.equals(current).not())
                            .or(playbackStatus == PlaybackStatus.ENDED)
                            .or(playbackStatus == PlaybackStatus.ERROR)) {
                PlaybackRequest.PLAY
            } else {
                if (playbackStatus == PlaybackStatus.PLAYING) PlaybackRequest.PAUSE
                else PlaybackRequest.RESUME
            }
        }
    }

    private fun handlePlaybackRequest(request: PlaybackRequest,
                                      playbackSource: PlaybackSource) {
        Timber.v("Applying playback change: $request for source: $playbackSource")

        // Apply the change to the source
        currentPlaybackSource = playbackSource

        // Apply the change to ExoPlayer
        when (request) {
            PlaybackRequest.PLAY -> exoPlayer.play(playbackSource.url)
            PlaybackRequest.PAUSE -> exoPlayer.pause()
            PlaybackRequest.RESUME -> exoPlayer.resume()
        }
    }

    private fun ExoPlayerState.toPlaybackStatus(): PlaybackStatus {
        return when (playbackState) {
            Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            Player.STATE_READY -> if (playWhenReady)
                PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            Player.STATE_ENDED -> PlaybackStatus.ENDED
            else -> throw IllegalStateException("Unsupported ExoPlayer status: $this")
        }
    }

    private enum class PlaybackRequest {
        PAUSE,
        RESUME,
        PLAY
    }

    private fun definePlayerTimePositionStream(): Observable<Long> {

        fun asUpdatingProgressOnceAndStream(updatePeriod: Long,
                                            timeUnit: TimeUnit) =
                Observable.timer(updatePeriod, timeUnit,
                                schedulerProvider.time(PLAYER_PROGRESS_SCHEDULER_TAG))
                        .observeOn(schedulerProvider.ui())
                        .repeat()
                        .startWith(0L)
                        .switchMap { exoPlayer.timePositionMsOnceAndStream }

        fun ExoPlayerState.isTimelineChanging() =
                playbackState == Player.STATE_READY && playWhenReady

        return exoPlayer.stateOnceAndStream
                .take(1) // when the state changes, the top-level Observable will re-evaluate
                .switchMap { state ->
                    if (state.isTimelineChanging())
                        asUpdatingProgressOnceAndStream(updatePeriod, timeUnit)
                    else
                        exoPlayer.timePositionMsOnceAndStream
                }
    }
}

