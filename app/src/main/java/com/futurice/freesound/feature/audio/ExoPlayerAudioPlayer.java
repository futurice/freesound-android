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

import com.futurice.freesound.common.functional.Functions;
import com.futurice.freesound.common.utils.Preconditions;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.subjects.PublishSubject;
import polanski.option.AtomicOption;
import polanski.option.Option;
import timber.log.Timber;

import static com.futurice.freesound.common.utils.Preconditions.get;

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
final class ExoPlayerAudioPlayer implements AudioPlayer {

    private enum ToggleAction {
        PAUSE,
        UNPAUSE,
        PLAY,
    }

    @NonNull
    private final ExoPlayer exoPlayer;

    @NonNull
    private final ObservableExoPlayer observableExoPlayer;

    @NonNull
    private final MediaSourceFactory mediaSourceFactory;

    @NonNull
    private final SerialDisposable playerStateDisposable = new SerialDisposable();

    @NonNull
    private final PublishSubject<PlaybackSource> toggleSourceStream = PublishSubject.create();

    @NonNull
    private final AtomicOption<PlaybackSource> currentSource = new AtomicOption<>();

    @Inject
    ExoPlayerAudioPlayer(@NonNull final ExoPlayer exoPlayer,
                         @NonNull final ObservableExoPlayer observableExoPlayer,
                         @NonNull final MediaSourceFactory mediaSourceFactory) {
        this.exoPlayer = get(exoPlayer);
        this.observableExoPlayer = get(observableExoPlayer);
        this.mediaSourceFactory = get(mediaSourceFactory);
    }

    @Override
    public void init() {
        playerStateDisposable
                .set(toggleSourceStream.concatMap(playbackSource -> observableExoPlayer
                        .getExoPlayerStateOnceAndStream()
                        .take(1)
                        .map(exoPlayerState -> toToggleAction(playbackSource, exoPlayerState))
                        .doOnNext(action -> handleToggleAction(action, playbackSource)))
                                       .subscribe(Functions.nothing1(),
                                                  e -> Timber
                                                          .e(e, "Fatal error toggling playback")));
    }

    @Override
    @NonNull
    public Observable<PlayerState> getPlayerStateOnceAndStream() {
        return observableExoPlayer.getExoPlayerStateOnceAndStream()
                                  .map(ExoPlayerAudioPlayer::toState)
                                  .map(state -> PlayerState.create(state, currentSource.get()));
    }

    @NonNull
    @Override
    public Observable<Long> getTimePositionMsOnceAndStream() {
        return observableExoPlayer.getTimePositionMsOnceAndStream(50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void togglePlayback(@NonNull PlaybackSource playbackSource) {
        Preconditions.checkNotNull(playbackSource);
        toggleSourceStream.onNext(playbackSource);
    }

    @Override
    public void stopPlayback() {
        stop();
    }

    @Override
    public void release() {
        playerStateDisposable.dispose();
        exoPlayer.release();
    }

    @NonNull
    private ToggleAction toToggleAction(@NonNull final PlaybackSource playbackSource,
                                        @NonNull final ExoPlayerState exoPlayerState) {
        if (isIdle(exoPlayerState.playbackState()) || hasSourceChanged(playbackSource)) {
            return ToggleAction.PLAY;
        } else {
            return exoPlayerState.playWhenReady() ? ToggleAction.PAUSE : ToggleAction.UNPAUSE;
        }
    }

    private void handleToggleAction(@NonNull final ToggleAction action,
                                    @NonNull final PlaybackSource playbackSource) {
        Timber.v("Action: %s, URL: %s", action, playbackSource);
        switch (action) {
            case PLAY:
                play(playbackSource);
                break;
            case PAUSE:
            case UNPAUSE:
                toggle(action);
                break;
            default:
                throw new IllegalArgumentException("Unsupported playback action: "
                                                   + action);
        }

    }

    private void play(@NonNull final PlaybackSource playbackSource) {
        exoPlayer.prepare(mediaSourceFactory.create(get(playbackSource.url())));
        exoPlayer.setPlayWhenReady(true);
        currentSource.set(Option.ofObj(playbackSource));
    }

    private void toggle(@NonNull final ToggleAction action) {
        exoPlayer.setPlayWhenReady(action != ToggleAction.PAUSE);
    }

    private void stop() {
        currentSource.set(Option.none());
        exoPlayer.stop();
    }

    private static boolean isIdle(final int state) {
        return state == ExoPlayer.STATE_IDLE || state == ExoPlayer.STATE_ENDED;
    }

    private boolean hasSourceChanged(@NonNull final PlaybackSource source) {
        return currentSource.get()
                            .map(playbackSource -> !playbackSource.equals(source))
                            .orDefault(() -> false);
    }

    @NonNull
    private static PlayerState.State toState(ExoPlayerState exoPlayerState) {
        int exoplaybackState = exoPlayerState.playbackState();
        if (exoplaybackState == ExoPlayer.STATE_IDLE) {
            return PlayerState.State.IDLE;
        } else if (exoplaybackState == ExoPlayer.STATE_ENDED) {
            return PlayerState.State.ENDED;
        } else if (exoplaybackState == ExoPlayer.STATE_BUFFERING) {
            return PlayerState.State.BUFFERING;
        } else if (exoplaybackState == ExoPlayer.STATE_READY) {
            return exoPlayerState.playWhenReady() ? PlayerState.State.PLAYING
                    : PlayerState.State.PAUSED;
        }
        return PlayerState.State.ERROR;
    }
}
