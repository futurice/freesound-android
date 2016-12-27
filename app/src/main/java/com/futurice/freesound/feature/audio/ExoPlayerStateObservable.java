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

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import android.support.annotation.NonNull;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Observer;
import timber.log.Timber;

final class ExoPlayerStateObservable extends Observable<PlayerState> {

    private final ExoPlayer exoPlayer;

    ExoPlayerStateObservable(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    @Override
    protected void subscribeActual(final Observer<? super PlayerState> observer) {
        Listener listener = new Listener(exoPlayer, observer);
        observer.onSubscribe(listener);
        exoPlayer.addListener(listener);
    }

    private static class Listener extends SimpleAudioPlayerEventListener<PlayerState> {

        Listener(@NonNull final ExoPlayer exoPlayer,
                 @NonNull Observer<? super PlayerState> observer) {
            super(exoPlayer, observer);
        }

        @Override
        public void onTracksChanged(final TrackGroupArray trackGroups,
                                    final TrackSelectionArray trackSelections) {
            Timber.d("### OnTracksChanged: %s", Arrays.toString(trackSelections.getAll()));
        }

        @Override
        public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
            observer.onNext(PlayerState.create(playWhenReady, playbackState));

        }

    }
}
