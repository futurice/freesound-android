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

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import com.futurice.freesound.common.Text;
import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.audio.PlaybackSource;
import com.futurice.freesound.feature.audio.PlayerState;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.Single;
import polanski.option.Option;

import static com.futurice.freesound.common.utils.Preconditions.get;
import static com.futurice.freesound.feature.audio.IdKt.from;
import static polanski.option.Option.ofObj;

@AutoFactory
final class SoundItemViewModel extends SimpleViewModel {

    @NonNull
    private final Sound sound;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final AudioPlayer audioPlayer;

    @NonNull
    private final FreeSoundApiService freeSoundApiService;

    SoundItemViewModel(@NonNull final Sound sound,
                       @Provided @NonNull final Navigator navigator,
                       @Provided @NonNull final AudioPlayer audioPlayer,
                       @Provided @NonNull final FreeSoundApiService freeSoundApiService) {
        this.sound = get(sound);
        this.navigator = get(navigator);
        this.audioPlayer = get(audioPlayer);
        this.freeSoundApiService = get(freeSoundApiService);
    }

    @NonNull
    Single<String> thumbnailImageUrl() {
        return Single.just(getThumbnail());
    }

    @NonNull
    private String getThumbnail() {
        return ofObj(sound.getImages())
                .flatMap(it -> ofObj(it.getMedSizeWaveformUrl()))
                .orDefault(() -> Text.EMPTY);
    }

    @NonNull
    Single<String> name() {
        return Single.just(sound.getName());
    }

    @NonNull
    Single<String> userAvatar() {
        return freeSoundApiService.getUser(sound.getUsername())
                                  .map(user -> user.getAvatar().getMedium())
                                  .cache();
    }

    @NonNull
    Single<String> createdDate() {
        return Single.just(sound.getCreated())
                     .map(Date::getTime)
                     .map(d -> DateFormat.getDateInstance().format(d));
    }

    @NonNull
    Single<String> username() {
        return Single.just(sound.getUsername());
    }

    @NonNull
    Single<String> description() {
        return Single.just(sound.getDescription());
    }

    @NonNull
    Single<Integer> duration() {
        return Single.just(sound.getDuration())
                     .map(duration -> (int) Math.ceil(duration))
                     .map(duration -> Math.max(duration, 1));
    }

    @NonNull
    Observable<Option<Integer>> progressPercentage() {
        return audioPlayer.getPlayerStateOnceAndStream()
                          .switchMap(this::progressOrNothing);
    }

    void openDetails() {
        navigator.openSoundDetails(sound);
    }

    void toggleSoundPlayback() {
        audioPlayer.togglePlayback(
                new PlaybackSource(from(sound.getId()),
                                   sound.getPreviews().getLowQualityMp3Url()));
    }

    @NonNull
    private Observable<Option<Integer>> progressOrNothing(@NonNull final PlayerState playerState) {
        return isThisSound(playerState) ? getCurrentPercentage() : Observable.just(Option.none());
    }

    private boolean isThisSound(@NonNull final PlayerState playerState) {
        return playerState.getSource()
                          .filter(playbackSource -> playbackSource.getId().equals(from(sound.getId())))
                          .isSome();
    }

    @NonNull
    private Observable<Option<Integer>> getCurrentPercentage() {
        return audioPlayer.getTimePositionMsOnceAndStream()
                          .map(positionMs -> toPercentage(positionMs, sound.getDuration()))
                          .map(Option::ofObj);
    }

    private static int toPercentage(final long positionMs,
                                    final float durationSec) {
        return Math.min(100, (int) ((positionMs / (durationSec * 1000.0F)) * 100L));
    }

}
