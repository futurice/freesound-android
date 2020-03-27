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

import com.futurice.freesound.test.rx.TimeSkipScheduler
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExoPlayerAudioPlayerTest {

    private lateinit var schedulerProvider: TrampolineSchedulerProvider
    private lateinit var fakeExoPlayer: FakeObservableExoPlayer
    private lateinit var exoPlayerAudioPlayer: ExoPlayerAudioPlayer

    private val updatePeriod = 100L
    private val updateUnits = TimeUnit.MILLISECONDS

    private fun TestScheduler.tick() {
        advanceTimeBy(updatePeriod, updateUnits)
        triggerActions()
    }

    @Before
    fun setUp() {
        schedulerProvider = TrampolineSchedulerProvider()
        fakeExoPlayer = FakeObservableExoPlayer()
        exoPlayerAudioPlayer = ExoPlayerAudioPlayer(fakeExoPlayer,
                updatePeriod,
                updateUnits,
                schedulerProvider)
    }

    @Test
    fun `playerState is Idle when ExoPlayer is Idle`() {
        // given
        exoPlayerAudioPlayer.init()

        // when, then
        exoPlayerAudioPlayer.playerStateOnceAndStream
                .test()
                .assertValue { state -> state is PlayerState.Idle }
    }

    @Test
    fun `togglePlayback follows playback sequence from Idle`() {
        // given
        val timerScheduler = TestScheduler()
        val playbackSource1 = PlaybackSource(Id("abc"), "url1")
        val playbackSource2 = PlaybackSource(Id("def"), "url2")
        ArrangeBuilder()
                .withTimeScheduler(timerScheduler)
        exoPlayerAudioPlayer.init()
        val states: TestObserver<in PlayerState> = exoPlayerAudioPlayer.playerStateOnceAndStream.test()

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource1) // play
        fakeExoPlayer.setProgress(500)
        timerScheduler.tick()
        exoPlayerAudioPlayer.togglePlayback(playbackSource1) // pause
        exoPlayerAudioPlayer.togglePlayback(playbackSource1) // resume
        fakeExoPlayer.setProgress(1000)
        timerScheduler.tick()
        exoPlayerAudioPlayer.togglePlayback(playbackSource2) // play different

        // then
        states.assertValues(PlayerState.Idle,
                PlayerState.Assigned(playbackSource1, PlaybackStatus.PLAYING, 0),
                PlayerState.Assigned(playbackSource1, PlaybackStatus.PLAYING, 500),
                PlayerState.Assigned(playbackSource1, PlaybackStatus.PAUSED, 500),
                PlayerState.Assigned(playbackSource1, PlaybackStatus.PLAYING, 500),
                PlayerState.Assigned(playbackSource1, PlaybackStatus.PLAYING, 1000),
                PlayerState.Assigned(playbackSource2, PlaybackStatus.PLAYING, 0))
    }

    @Test
    fun `togglePlayback plays URL when is Idle`() {
        // given
        val playbackSource = PlaybackSource(Id("abc"), "url")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
        exoPlayerAudioPlayer.init()

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `togglePlayback plays URL when is Ended`() {
        // given
        val playbackSource = PlaybackSource(Id("abc"), "url")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withEndedExoPlayer(playbackSource)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `togglePlayback pauses when same URL is Playing`() {
        // given
        val playbackSource = PlaybackSource(Id("abc"), "url")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withPlayingExoPlayer(playbackSource)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource,
                            PlaybackStatus.PAUSED, 0)
                }
    }

    @Test
    fun `togglePlayback resumes URL when same URL is Paused`() {
        // given
        val playbackSource = PlaybackSource(Id("abc"), "url")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withPausedExoPlayer(playbackSource)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `togglePlayback plays new URL when different URL is Playing`() {
        // given
        val playbackSource1 = PlaybackSource(Id("abc"), "url1")
        val playbackSource2 = PlaybackSource(Id("def"), "url2")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withPlayingExoPlayer(playbackSource1)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource2)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource2,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `togglePlayback plays URL different URL is Paused`() {
        // given
        val playbackSource1 = PlaybackSource(Id("abc"), "url1")
        val playbackSource2 = PlaybackSource(Id("def"), "url2")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withPausedExoPlayer(playbackSource1)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource2)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource2,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `togglePlayback plays URL different URL is Ended`() {
        // given
        val playbackSource1 = PlaybackSource(Id("abc"), "url1")
        val playbackSource2 = PlaybackSource(Id("def"), "url2")
        ArrangeBuilder()
                .withTimeScheduler(TestScheduler())
                .withEndedExoPlayer(playbackSource1)

        // when
        exoPlayerAudioPlayer.togglePlayback(playbackSource2)

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream.test()
                .assertValue {
                    it == PlayerState.Assigned(playbackSource2,
                            PlaybackStatus.PLAYING, 0)
                }
    }

    @Test
    fun `stopPlayback stops ExoPlayer`() {
        // given
        ArrangeBuilder()
                .withPlayingExoPlayer(PlaybackSource(Id("abc"), "url"))

        // when
        exoPlayerAudioPlayer.stopPlayback()

        // then
        exoPlayerAudioPlayer.playerStateOnceAndStream
                .test()
                .assertValue { it is PlayerState.Idle }
    }

    @Test
    fun `release releases ExoPlayer`() {
        // given, when
        exoPlayerAudioPlayer.release()

        // then
        assertThat(fakeExoPlayer.isReleased).isTrue()
    }

    private inner class ArrangeBuilder() {

        init {
            withTimeSkipScheduler()
        }

        fun withPlayingExoPlayer(playbackSource: PlaybackSource): ArrangeBuilder {
            exoPlayerAudioPlayer.init()
            exoPlayerAudioPlayer.togglePlayback(playbackSource)
            return this;
        }

        fun withPausedExoPlayer(playbackSource: PlaybackSource): ArrangeBuilder {
            exoPlayerAudioPlayer.init()
            exoPlayerAudioPlayer.togglePlayback(playbackSource) // make play
            exoPlayerAudioPlayer.togglePlayback(playbackSource) // make pause
            return this;
        }

        fun withEndedExoPlayer(playbackSource: PlaybackSource): ArrangeBuilder {
            exoPlayerAudioPlayer.init()
            exoPlayerAudioPlayer.togglePlayback(playbackSource) // make play
            fakeExoPlayer.end() // make end
            return this;
        }

        fun withTimeScheduler(scheduler: Scheduler): ArrangeBuilder {
            schedulerProvider.setTimeScheduler({ scheduler })
            return this
        }

        fun withTimeSkipScheduler(): ArrangeBuilder {
            return withTimeScheduler(TimeSkipScheduler.instance())
        }

    }

}
