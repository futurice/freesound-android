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

package com.futurice.freesound.feature.audio;

import com.google.android.exoplayer2.ExoPlayer;

import com.futurice.freesound.common.rx.TimeScheduler;
import com.futurice.freesound.common.rx.TimeSkipScheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;

public class DefaultObservableExoPlayerTest {

    private static final ExoPlayerState TEST_INITIAL_EXOPLAYER_STATE =
            ExoPlayerState.create(true, ExoPlayer.STATE_IDLE);

    private static final long TEST_INITIAL_EXOPLAYER_PROGRESS = 0L;

    private BehaviorSubject<ExoPlayerState> exoPlayerStateOnceAndStream = BehaviorSubject.create();

    private BehaviorSubject<Long> exoPlayerProgressObservable = BehaviorSubject.create();

    private DefaultObservableExoPlayer defaultObservableExoPlayer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        defaultObservableExoPlayer = new DefaultObservableExoPlayer(exoPlayerStateOnceAndStream,
                                                                    exoPlayerProgressObservable);
    }

    @Test
    public void getExoPlayerStateOnceAndStream_emitsObservableValue() {
        new ArrangeBuilder()
                .withExoPlayerState(TEST_INITIAL_EXOPLAYER_STATE);

        defaultObservableExoPlayer.getExoPlayerStateOnceAndStream()
                                  .test()
                                  .assertValue(TEST_INITIAL_EXOPLAYER_STATE);
    }

    @Test
    public void getTimePositionMsOnceAndStream_reportsSingleInitialProgressWhenIdle() {
        new ArrangeBuilder()
                .withIdleExoPlayer()
                .withProgress(500L);

        defaultObservableExoPlayer.getTimePositionMsOnceAndStream(100L, TimeUnit.SECONDS)
                                  .test()
                                  .assertValue(500L);

    }

    @Test
    public void getTimePositionMsOnceAndStream_reportsInitialProgress_whenPaused() {
        new ArrangeBuilder()
                .withPausedExoPlayer()
                .withProgress(500L);

        defaultObservableExoPlayer.getTimePositionMsOnceAndStream(100L, TimeUnit.SECONDS)
                                  .test()
                                  .assertValue(500L);
    }

    @Test
    public void getTimePositionMsOnceAndStream_reportsInitialProgress_whenEnded() {
        new ArrangeBuilder()
                .withEndedExoPlayer()
                .withProgress(500L);

        defaultObservableExoPlayer.getTimePositionMsOnceAndStream(100L, TimeUnit.SECONDS)
                                  .test()
                                  .assertValue(500L);
    }

    @Test
    public void getTimePositionMsOnceAndStream_reportsInitialProgress_whenPlaying() {
        TestScheduler testScheduler = new TestScheduler();
        new ArrangeBuilder()
                .withTimeScheduler(testScheduler)
                .withPlayingExoPlayer()
                .withProgress(500L);

        testScheduler.triggerActions();

        defaultObservableExoPlayer.getTimePositionMsOnceAndStream(100L, TimeUnit.SECONDS)
                                  .test()
                                  .assertValue(500L);

    }

    @Test
    public void getTimePositionMsOnceAndStream_reportsProgressValue_whenPlaying_onEachUpdatePeriod() {
        TestScheduler testScheduler = new TestScheduler();
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withTimeScheduler(testScheduler)
                .withPlayingExoPlayer()
                .withProgress(500L);

        TestObserver<Long> testObserver = defaultObservableExoPlayer
                .getTimePositionMsOnceAndStream(100L, TimeUnit.SECONDS)
                .test();
        testScheduler.advanceTimeBy(50L, TimeUnit.SECONDS);
        arrangeBuilder.withProgress(1000L);

        testObserver.assertValues(500L, 1000L);
    }

    private class ArrangeBuilder {

        public ArrangeBuilder() {
            exoPlayerStateOnceAndStream.onNext(TEST_INITIAL_EXOPLAYER_STATE);
            exoPlayerProgressObservable.onNext(TEST_INITIAL_EXOPLAYER_PROGRESS);
            withTimeSkipScheduler();
        }

        ArrangeBuilder withExoPlayerState(ExoPlayerState state) {
            exoPlayerStateOnceAndStream.onNext(state);
            return this;
        }

        ArrangeBuilder withIdleExoPlayer() {
            return withExoPlayerStateStreamEvent(false, ExoPlayer.STATE_IDLE);
        }

        ArrangeBuilder withPlayingExoPlayer() {
            return withExoPlayerStateStreamEvent(true, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withPausedExoPlayer() {
            return withExoPlayerStateStreamEvent(false, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withEndedExoPlayer() {
            return withExoPlayerStateStreamEvent(true, ExoPlayer.STATE_ENDED);
        }

        ArrangeBuilder withExoPlayerStateStreamEvent(boolean playWhenReady, int state) {
            exoPlayerStateOnceAndStream.onNext(ExoPlayerState.create(playWhenReady, state));
            return this;
        }

        ArrangeBuilder withProgress(long progress) {
            exoPlayerProgressObservable.onNext(progress);
            return this;
        }

        ArrangeBuilder withTimeScheduler(Scheduler scheduler) {
            TimeScheduler.setTimeSchedulerHandler((s, __) -> scheduler);
            return this;
        }

        ArrangeBuilder withTimeSkipScheduler() {
            return withTimeScheduler(TimeSkipScheduler.instance());
        }

    }

}

