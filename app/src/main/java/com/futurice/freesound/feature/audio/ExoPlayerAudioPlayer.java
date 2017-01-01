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

import com.futurice.freesound.common.Releaseable;
import com.futurice.freesound.utils.Preconditions;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reactivex.Observable;
import polanski.option.AtomicOption;
import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.get;

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
 */
final class ExoPlayerAudioPlayer implements Releaseable, AudioPlayer {

    @NonNull
    private final ExoPlayer exoPlayer;

    @NonNull
    private final MediaSourceFactory mediaSourceFactory;

    @NonNull
    private final AtomicOption<String> currentUrl = new AtomicOption<>();

    @Inject
    ExoPlayerAudioPlayer(@NonNull final ExoPlayer exoPlayer,
                         @NonNull final MediaSourceFactory mediaSourceFactory) {
        this.exoPlayer = get(exoPlayer);
        this.mediaSourceFactory = get(mediaSourceFactory);
    }

    @Override
    @NonNull
    public Observable<UrlPlayerState> getPlayerStateStream() {
        return ExoPlayerObservables.playerState(exoPlayer)
                                   .startWith(currentPlayerState())
                                   .map(state -> UrlPlayerState.create(state, currentUrl.get()));
    }

    @Override
    public void toggle(@NonNull final String url) {
        Preconditions.checkNotNull(url);

        final int state = exoPlayer.getPlaybackState();
        final boolean hasSourceUrlChanged = hasSourceUrlChanged(url);

        currentUrl.set(Option.ofObj(url));

        if (isIdle(state) || hasSourceUrlChanged) {
            exoPlayer.prepare(mediaSourceFactory.create(get(url)));
            exoPlayer.setPlayWhenReady(true);
        } else {
            exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
        }

    }

    @Override
    public void stop() {
        currentUrl.set(Option.none());
        exoPlayer.stop();
    }

    @Override
    public void release() {
        exoPlayer.release();
    }

    @NonNull
    private PlayerState currentPlayerState() {
        return PlayerState.create(exoPlayer.getPlayWhenReady(), exoPlayer.getPlaybackState());
    }

    private static boolean isIdle(final int state) {
        return state == ExoPlayer.STATE_IDLE || state == ExoPlayer.STATE_ENDED;
    }

    private boolean hasSourceUrlChanged(@NonNull final String url) {
        return currentUrl.get()
                         .map(id -> !id.equals(url))
                         .orDefault(() -> false);
    }
}
