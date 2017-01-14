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

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class DefaultObservableExoPlayer implements ObservableExoPlayer {

    @NonNull
    private final ExoPlayer exoPlayer;

    @Inject
    DefaultObservableExoPlayer(@NonNull final ExoPlayer exoPlayer) {
        this.exoPlayer = get(exoPlayer);
    }

    @NonNull
    @Override
    public Observable<ExoPlayerState> getExoPlayerStateOnceAndStream() {
        return new ExoPlayerStateObservable(exoPlayer);
    }

    @NonNull
    @Override
    public Observable<Long> getTimePositionMsOnceAndStream() {
        return new ExoPlayerProgressObservable(exoPlayer);
    }
}
