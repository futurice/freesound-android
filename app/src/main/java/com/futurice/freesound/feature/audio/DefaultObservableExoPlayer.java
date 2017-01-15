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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class DefaultObservableExoPlayer implements ObservableExoPlayer {

    @VisibleForTesting
    final String PLAYER_PROGRESS_SCHEDULER_TAG = "PLAYER_PROGRESS_SCHEDULER";

    @NonNull
    private final Observable<ExoPlayerState> stateOnceAndStream;

    @NonNull
    private final Observable<Long> progressOnceAndStream;

    @Inject
    DefaultObservableExoPlayer(
            @NonNull final Observable<ExoPlayerState> stateOnceAndStream,
            @NonNull final Observable<Long> progressOnceAndStream) {
        this.stateOnceAndStream = get(stateOnceAndStream);
        this.progressOnceAndStream = get(progressOnceAndStream);
    }

    @NonNull
    @Override
    public Observable<ExoPlayerState> getExoPlayerStateOnceAndStream() {
        return stateOnceAndStream;
    }

    @NonNull
    @Override
    public Observable<Long> getTimePositionMsOnceAndStream(long updatePeriod, TimeUnit timeUnit) {
        return getExoPlayerStateOnceAndStream()
                .map(DefaultObservableExoPlayer::isTimelineChanging)
                .switchMap(isTimelineChanging -> timePositionMsOnceAndStream(isTimelineChanging,
                                                                             updatePeriod,
                                                                             timeUnit));
    }

    @NonNull
    private Observable<Long> timePositionMsOnceAndStream(
            final boolean isTimelineChanging, final long updatePeriod, final TimeUnit timeUnit) {
        return isTimelineChanging ?
                Observable.timer(updatePeriod, timeUnit,
                                 TimeScheduler.time(PLAYER_PROGRESS_SCHEDULER_TAG))
                          .repeat()
                          .startWith(0L)
                          .switchMap(__ -> progressOnceAndStream)
                : progressOnceAndStream;
    }

    private static boolean isTimelineChanging(@NonNull final ExoPlayerState playerState) {
        return playerState.playbackState() == ExoPlayer.STATE_READY && playerState.playWhenReady();
    }
}
