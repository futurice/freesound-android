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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundImageFormat;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import io.reactivex.Single;

import static com.futurice.freesound.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

final class SoundItemViewModel extends SimpleViewModel {

    @NonNull
    private final Sound sound;

    @NonNull
    private final Navigator navigator;

    SoundItemViewModel(@NonNull final Sound sound,
                       @NonNull final Navigator navigator) {
        this.sound = get(sound);
        this.navigator = get(navigator);
    }

    @NonNull
    Single<String> thumbnailImageUrl() {
        return Single.just(getThumbnail());
    }

    @NonNull
    private String getThumbnail() {
        return ofObj(sound.images())
                .flatMap(it -> ofObj(it.get(SoundImageFormat.waveform_m)))
                .orDefault(() -> "");
    }

    @NonNull
    Single<String> name() {
        return Single.just(sound.name());
    }

    @NonNull
    Single<String> description() {
        return Single.just(sound.description());
    }

    void openDetails() {
        navigator.openSoundDetails(sound);
    }

}
