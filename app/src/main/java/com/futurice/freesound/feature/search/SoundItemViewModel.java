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

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import com.futurice.freesound.common.Text;
import com.futurice.freesound.feature.audio.MediaSourceFactory;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundPreviewFormat;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.net.Uri;
import android.support.annotation.NonNull;

import io.reactivex.Single;

import static com.futurice.freesound.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

@AutoFactory
final class SoundItemViewModel extends SimpleViewModel {

    @NonNull
    private final Sound sound;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final SimpleExoPlayer exoplayer;

    @NonNull
    private final MediaSourceFactory mediaSourceFactory;

    SoundItemViewModel(@NonNull final Sound sound,
                       @Provided @NonNull final Navigator navigator,
                       @NonNull final SimpleExoPlayer exoPlayer,
                       @NonNull final MediaSourceFactory mediaSourceFactory) {
        this.sound = get(sound);
        this.navigator = get(navigator);
        this.exoplayer = get(exoPlayer);
        this.mediaSourceFactory = get(mediaSourceFactory);
    }

    @NonNull
    Single<String> thumbnailImageUrl() {
        return Single.just(getThumbnail());
    }

    @NonNull
    private String getThumbnail() {
        return ofObj(sound.images())
                .flatMap(it -> ofObj(it.medSizeWaveformUrl()))
                .orDefault(() -> Text.EMPTY);
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

    void playPreview() {
        exoplayer.prepare(mediaSourceFactory.create(Uri.parse(
                sound.previews().get(SoundPreviewFormat.preview_lq_mp3))));
        exoplayer.setPlayWhenReady(true);
    }

    public void stopPreview() {
        exoplayer.stop();
    }
}
