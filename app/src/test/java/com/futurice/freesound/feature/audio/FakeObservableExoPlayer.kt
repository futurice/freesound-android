package com.futurice.freesound.feature.audio

import com.google.android.exoplayer2.Player
import io.reactivex.subjects.BehaviorSubject

class FakeObservableExoPlayer(override val stateOnceAndStream: BehaviorSubject<ExoPlayerState>
                              = BehaviorSubject.createDefault(ExoPlayerState(true, Player.STATE_IDLE)),
                              override var timePositionMsOnceAndStream: BehaviorSubject<Long>
                              = BehaviorSubject.createDefault(0)) : ObservableExoPlayer {

    var isReleased: Boolean = false; private set

    override fun play(url: String) {
        checkIsNotReleased()
        timePositionMsOnceAndStream = BehaviorSubject.createDefault(0)
        stateOnceAndStream.onNext(ExoPlayerState(true, Player.STATE_READY))
    }

    private fun checkIsNotReleased() {
        require(!isReleased, { "Player has already been released" })
    }

    override fun stop() {
        checkIsNotReleased()
        stateOnceAndStream.onNext(ExoPlayerState(true, Player.STATE_IDLE))
    }

    override fun pause() {
        checkIsNotReleased()
        stateOnceAndStream.onNext(ExoPlayerState(false, Player.STATE_READY))
    }

    override fun resume() {
        checkIsNotReleased()
        stateOnceAndStream.onNext(ExoPlayerState(true, Player.STATE_READY))
    }

    override fun release() {
        isReleased = true
    }

    fun end() {
        checkIsNotReleased()
        stateOnceAndStream.onNext(ExoPlayerState(true, Player.STATE_ENDED))
    }

    fun buffer() {
        checkIsNotReleased()
        stateOnceAndStream.onNext(ExoPlayerState(true, Player.STATE_BUFFERING))
    }

    fun setProgress(progress: Long) {
        checkIsNotReleased()
        // Because progress is derived through polling, we don't just emit another values.
        // Instead we update the latent value and then assume that the AudioPlayer will resubscribe
        // to get the new value.
        timePositionMsOnceAndStream = BehaviorSubject.createDefault(progress)
    }

}
