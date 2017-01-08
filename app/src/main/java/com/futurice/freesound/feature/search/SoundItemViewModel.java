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
import com.futurice.freesound.feature.audio.PlayerState;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import polanski.option.Option;
import timber.log.Timber;

import static com.futurice.freesound.common.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

@AutoFactory
final class SoundItemViewModel extends SimpleViewModel {

    @NonNull
    private final Sound sound;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final AudioPlayer audioPlayer;

    SoundItemViewModel(@NonNull final Sound sound,
                       @Provided @NonNull final Navigator navigator,
                       @Provided @NonNull final AudioPlayer audioPlayer) {
        this.sound = get(sound);
        this.navigator = get(navigator);
        this.audioPlayer = get(audioPlayer);
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

    @NonNull
    Single<Integer> duration() {
        return Single.just(sound.duration())
                     .map(duration -> (int) Math.ceil(duration))
                     .map(duration -> Math.max(duration, 1));
    }

    @NonNull
    Observable<Option<Integer>> progressPercentage() {

        return audioPlayer.getPlayerStateOnceAndStream()
                          .switchMap(
                                  new Function<PlayerState, ObservableSource<? extends Option<Integer>>>() {
                                      @Override
                                      public ObservableSource<? extends Option<Integer>> apply(
                                              final PlayerState playerState)
                                              throws Exception {
                                          return playerState.id()
                                                            .log("PLATSTATE", n -> Timber
                                                                    .d("### is: %s for %s", n,
                                                                       sound))
                                                            .filter(id -> id.equals(sound.previews()
                                                                                         .lowQualityMp3Url()))
                                                            .isSome() ?

                                                  Observable.timer(1, TimeUnit.SECONDS)
                                                            .repeat()
                                                            .startWith(0L)
                                                            .switchMap(__ ->
                                                                               audioPlayer
                                                                                       .getTimePositionMsOnceAndStream()
                                                                                       .doOnEach(
                                                                                               e -> Timber
                                                                                                       .d("### time position changed %s",
                                                                                                          e))
                                                                                       .map(positionMs -> toPercentage(
                                                                                               positionMs,
                                                                                               sound.duration()))
                                                                                       .map(Option::ofObj))
                                                  : Observable.just(Option.none());
                                      }
                                  });
    }

    private Integer toPercentage(final Long positionMs, final Float durationSec) {
        return (int) ((positionMs / (durationSec * 1000.0)) * 100);
    }

    void openDetails() {
        navigator.openSoundDetails(sound);
    }

    void toggleSoundPlayback() {
        audioPlayer.togglePlayback(sound.previews().lowQualityMp3Url());
    }

}
