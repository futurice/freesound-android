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

class ExoPlayerStateObservableFactory {

    @Inject
    ExoPlayerStateObservableFactory() {
    }

    @NonNull
    public Observable<ExoPlayerState> create(@NonNull final ExoPlayer exoPlayer) {
        return new ExoPlayerStateObservable(exoPlayer);
    }
}
