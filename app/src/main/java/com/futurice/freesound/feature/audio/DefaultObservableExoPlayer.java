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

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class DefaultObservableExoPlayer implements ObservableExoPlayer {

    @NonNull
    private final ExoPlayerStateObservableFactory stateObservableFactory;

    @NonNull
    private final ExoPlayerProgressObservableFactory progressObservableFactory;

    @Inject
    DefaultObservableExoPlayer(
            @NonNull final ExoPlayerStateObservableFactory stateObservableFactory,
            @NonNull final ExoPlayerProgressObservableFactory progressObservableFactory) {
        this.stateObservableFactory = get(stateObservableFactory);
        this.progressObservableFactory = get(progressObservableFactory);
    }

    @NonNull
    @Override
    public Observable<ExoPlayerState> getExoPlayerStateOnceAndStream() {
        return stateObservableFactory.create();
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
                Observable.timer(updatePeriod, timeUnit)
                          .repeat()
                          .startWith(0L)
                          .switchMap(__ -> progressObservableFactory.create())
                : progressObservableFactory.create();
    }

    private static boolean isTimelineChanging(final ExoPlayerState playerState) {
        return playerState.playbackState() == ExoPlayer.STATE_READY && playerState.playWhenReady();
    }
}
