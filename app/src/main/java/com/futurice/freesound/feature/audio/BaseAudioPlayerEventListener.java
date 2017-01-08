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

package com.futurice.freesound.feature.audio;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.futurice.freesound.utils.Preconditions.get;

/**
 * Base class for making Observables from ExoPlayer callback events.
 *
 * @param <T> the Observable value type.
 */
abstract class BaseAudioPlayerEventListener<T> implements Disposable, ExoPlayer.EventListener {

    @NonNull
    protected final ExoPlayer exoPlayer;

    @NonNull
    protected final Observer<? super T> observer;

    @NonNull
    private final AtomicBoolean unsubscribed = new AtomicBoolean();

    BaseAudioPlayerEventListener(@NonNull final ExoPlayer exoPlayer,
                                 @NonNull final Observer<? super T> observer) {
        this.exoPlayer = get(exoPlayer);
        this.observer = get(observer);
    }

    @Override
    public void onLoadingChanged(final boolean isLoading) {
        // Override if needed
    }

    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        // Override if needed
    }

    @Override
    public void onTimelineChanged(final Timeline timeline, final Object manifest) {
        // Override if needed
    }

    @Override
    public void onPlayerError(final ExoPlaybackException error) {
        // Override if needed
    }

    @Override
    public void onPositionDiscontinuity() {
        // Override if needed
    }

    @Override
    public void onTracksChanged(final TrackGroupArray trackGroups,
                                final TrackSelectionArray trackSelections) {
        // Override if needed
    }

    @Override
    public final void dispose() {
        if (unsubscribed.compareAndSet(false, true)) {
            exoPlayer.removeListener(this);
        }
    }

    @Override
    public final boolean isDisposed() {
        return unsubscribed.get();
    }
}
