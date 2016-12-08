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

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

@AutoValue
public abstract class PlayerState {

    abstract boolean playWhenReady();

    /**
     * See {@link com.google.android.exoplayer2.ExoPlayer} {@code STATE}
     */
    abstract int playbackState();

    @NonNull
    static PlayerState create(final boolean playWhenReady, final int playbackState) {
        return new AutoValue_PlayerState(playWhenReady, playbackState);
    }
}
