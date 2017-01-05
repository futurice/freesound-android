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

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ExoPlayerStateObservableTest {

    @Mock
    private ExoPlayer exoPlayer;

    @Captor
    private ArgumentCaptor<ExoPlayer.EventListener> listenerCaptor;

    private ExoPlayerStateObservable exoPlayerStateObservable;

    public ExoPlayerStateObservableTest() {
        MockitoAnnotations.initMocks(this);
        exoPlayerStateObservable = new ExoPlayerStateObservable(exoPlayer, false);
    }

    @Test
    public void doesNothingToExoPlayer_beforeSubscribed() {
        verifyZeroInteractions(exoPlayer);
    }

    @Test
    public void addsListener_whenSubscribing() {
        exoPlayerStateObservable.subscribe();

        verify(exoPlayer).addListener(any(ExoPlayer.EventListener.class));
    }

    @Test
    public void removesListener_whenUnsubscribing() {
        exoPlayerStateObservable.subscribe().dispose();

        verify(exoPlayer).removeListener(any(ExoPlayer.EventListener.class));
    }

    @Test
    public void emitsCallbackValue() {
        TestObserver<ExoPlayerState> testObserver = exoPlayerStateObservable.test();

        new ExoPlayerTestEventGenerator()
                .invokeListenerCallback(true, ExoPlayer.STATE_IDLE);

        testObserver.assertValue(ExoPlayerState.create(true, ExoPlayer.STATE_IDLE))
                    .assertNotTerminated();
    }

    @Test
    public void doesNotEmitAfterDisposed() {
        TestObserver<ExoPlayerState> testObserver = exoPlayerStateObservable.test();
        testObserver.dispose();

        new ExoPlayerTestEventGenerator()
                .invokeListenerCallback(true, ExoPlayer.STATE_IDLE);

        testObserver.assertNoValues();
    }

    // Special tests for initial emit

    @Test
    public void doesNotEmitInitialValue_whenNotSet() {
        when(exoPlayer.getPlayWhenReady()).thenReturn(true);
        when(exoPlayer.getPlaybackState()).thenReturn(1000);
        ExoPlayerStateObservable observable = new ExoPlayerStateObservable(exoPlayer, false);

        TestObserver<ExoPlayerState> testObserver = observable.test();

        testObserver.assertNoValues();
    }

    @Test
    public void emitsInitialValue_whenSet() {
        when(exoPlayer.getPlayWhenReady()).thenReturn(true);
        when(exoPlayer.getPlaybackState()).thenReturn(1000);
        ExoPlayerStateObservable observable = new ExoPlayerStateObservable(exoPlayer, true);

        TestObserver<ExoPlayerState> testObserver = observable.test();

        testObserver.assertValue(ExoPlayerState.create(true, 1000));
    }

    // Helpers

    private class ExoPlayerTestEventGenerator {

        ExoPlayerTestEventGenerator() {
            verify(exoPlayer).addListener(listenerCaptor.capture());
        }

        ExoPlayerTestEventGenerator invokeListenerCallback(boolean playWhenReady,
                                                           int playbackState) {
            listenerCaptor.getValue().onPlayerStateChanged(playWhenReady, playbackState);
            return this;
        }

    }

}
