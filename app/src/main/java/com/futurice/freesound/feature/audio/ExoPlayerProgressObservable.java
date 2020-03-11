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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;

import static com.futurice.freesound.common.utils.Preconditions.get;

/**
 * Make an Observable from the ExoPlayer playback progress.
 * <p>
 * Based upon techniques used in the RxBinding library.
 * <p>
 * Note: There's no callback notification trigger only when the playback progress updates.
 * This means that consumers will need to resubscribe whenever they want to check the progress
 * updates. It's not much of an Observable!
 */
final class ExoPlayerProgressObservable extends Observable<Long> {

    @NonNull
    private final ExoPlayer exoPlayer;

    private final boolean emitInitial;

    @Inject
    ExoPlayerProgressObservable(@NonNull final ExoPlayer exoPlayer) {
        this(exoPlayer, true);
    }

    ExoPlayerProgressObservable(@NonNull final ExoPlayer exoPlayer,
                                final boolean emitInitial) {
        this.exoPlayer = get(exoPlayer);
        this.emitInitial = emitInitial;
    }

    @Override
    protected void subscribeActual(final Observer<? super Long> observer) {
        Listener listener = new Listener(exoPlayer, observer);
        observer.onSubscribe(listener);
        exoPlayer.addListener(listener);
        if (emitInitial) {
            emitValue(exoPlayer, observer);
        }
    }

    private static class Listener extends BaseAudioPlayerEventListener<Long> {

        Listener(@NonNull final ExoPlayer exoPlayer,
                 @NonNull final Observer<? super Long> observer) {
            super(exoPlayer, observer);
        }

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            safeEmitValue();
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            safeEmitValue();
        }

        @Override
        public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
            safeEmitValue();
        }

        private void safeEmitValue() {
            if (!isDisposed()) {
                emitValue(exoPlayer, observer);
            }
        }
    }

    private static void emitValue(@NonNull final ExoPlayer exoPlayer,
                                  @NonNull final Observer<? super Long> observer) {
        observer.onNext(exoPlayer.getCurrentPosition());
    }
}
