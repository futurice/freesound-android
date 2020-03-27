package com.futurice.freesound.feature.audio

import com.google.android.exoplayer2.ExoPlayer
import io.reactivex.Observable

internal class DefaultObservableExoPlayer(private val exoPlayer: ExoPlayer,
                                          private val mediaSourceFactory: MediaSourceFactory) : ObservableExoPlayer {

    override val stateOnceAndStream: Observable<ExoPlayerState>
        get() = ExoPlayerStateObservable(exoPlayer);

    override val timePositionMsOnceAndStream: Observable<Long>
        get() = ExoPlayerProgressObservable(exoPlayer)

    override fun play(url: String) {
        exoPlayer.prepare(mediaSourceFactory.create(url))
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun resume() {
        exoPlayer.playWhenReady = true
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        exoPlayer.release()
    }

}
