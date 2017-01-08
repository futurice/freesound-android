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

public class ExoPlayerProgressObservableTest {

    @Mock
    private ExoPlayer exoPlayer;

    @Captor
    private ArgumentCaptor<ExoPlayer.EventListener> listenerCaptor;

    private ExoPlayerProgressObservable exoPlayerProgressObservable;

    public ExoPlayerProgressObservableTest() {
        MockitoAnnotations.initMocks(this);
        exoPlayerProgressObservable = new ExoPlayerProgressObservable(exoPlayer, false);
    }

    @Test
    public void doesNothingToExoPlayer_beforeSubscribed_whenNoEmittingInitialValue() {
        verifyZeroInteractions(exoPlayer);
    }

    @Test
    public void addsListener_whenSubscribing() {
        exoPlayerProgressObservable.subscribe();

        verify(exoPlayer).addListener(any(ExoPlayer.EventListener.class));
    }

    @Test
    public void removesListener_whenUnsubscribing() {
        exoPlayerProgressObservable.subscribe().dispose();

        verify(exoPlayer).removeListener(any(ExoPlayer.EventListener.class));
    }

    @Test
    public void emitsCallbackValue() {
        long expected = 100L;
        TestObserver<Long> testObserver = exoPlayerProgressObservable.test();

        new ExoPlayerTestEventGenerator()
                .moveToProgressTime(expected)
                .invokeListenerCallback();

        testObserver.assertValue(expected)
                    .assertNotTerminated();
    }

    @Test
    public void doesNotEmitAfterDisposed() {
        TestObserver<Long> testObserver = exoPlayerProgressObservable.test();
        testObserver.dispose();

        new ExoPlayerTestEventGenerator()
                .invokeListenerCallback();

        testObserver.assertNoValues();
    }

    // Special tests for initial emit

    @Test
    public void doesNotEmitInitialValue_whenNotSet() {
        ExoPlayerProgressObservable observable = new ExoPlayerProgressObservable(exoPlayer, false);

        TestObserver<Long> testObserver = observable.test();

        testObserver.assertNoValues();
    }

    @Test
    public void emitsInitialValue_whenSet() {
        long expected = 1000L;
        when(exoPlayer.getCurrentPosition()).thenReturn(expected);
        ExoPlayerProgressObservable observable = new ExoPlayerProgressObservable(exoPlayer, true);

        TestObserver<Long> testObserver = observable.test();

        testObserver.assertValue(expected);
    }

    // Helpers

    private class ExoPlayerTestEventGenerator {

        ExoPlayerTestEventGenerator() {
            verify(exoPlayer).addListener(listenerCaptor.capture());
        }

        ExoPlayerTestEventGenerator moveToProgressTime(long progress) {
            when(exoPlayer.getCurrentPosition()).thenReturn(progress);
            return this;
        }

        ExoPlayerTestEventGenerator invokeListenerCallback() {
            // don't care about value and are nullable anyway
            listenerCaptor.getValue().onTimelineChanged(null, null);
            return this;
        }

    }

}
