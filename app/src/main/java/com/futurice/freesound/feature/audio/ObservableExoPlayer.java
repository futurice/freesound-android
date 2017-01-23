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

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Represent the {@link com.google.android.exoplayer2.ExoPlayer} through Observables.
 */
interface ObservableExoPlayer {

    /**
     * ExoPlayer state {@link Observable} with initial value.
     *
     * @return the Observable {@link ExoPlayerState}.
     */
    @NonNull
    Observable<ExoPlayerState> getExoPlayerStateOnceAndStream();

    /**
     * ExoPlayer current playback time position with initial value.
     *
     * @return the Observable playback time position in milliseconds.
     */
    @NonNull
    Observable<Long> getTimePositionMsOnceAndStream(long update, TimeUnit timeUnit);
}
