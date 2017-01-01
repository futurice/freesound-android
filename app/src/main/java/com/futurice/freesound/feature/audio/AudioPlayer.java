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

import android.support.annotation.NonNull;

import io.reactivex.Observable;

/**
 * An AudioPlayer for URL based sources.
 */
public interface AudioPlayer {

    /**
     * Reports the initial player state and subsequent changes.
     *
     * @return A stream of the {@link UrlPlayerState}.
     */
    @NonNull
    Observable<UrlPlayerState> getPlayerStateStream();

    /**
     * Toggles the playback for the given URL.
     *
     * Starts playback if currently playing, otherwise will pause.
     *
     * @param url the audio source URL.
     */
    void toggle(@NonNull String url);

    /**
     * Stops the current playback and reset state.
     */
    void stop();
}
