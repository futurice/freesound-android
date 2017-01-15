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

import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.audio.Id;
import com.futurice.freesound.feature.audio.PlaybackSource;
import com.futurice.freesound.feature.audio.PlayerState;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SoundItemViewModelTest {

    private static final Sound TEST_SOUND = TestData.sound(1L);

    @Mock
    private Navigator navigator;

    @Mock
    private AudioPlayer audioPlayer;

    private SoundItemViewModel soundItemViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        soundItemViewModel = new SoundItemViewModel(TEST_SOUND, navigator, audioPlayer);
    }

    @Test
    public void thumbnailImageUrl_ifNoImages_returnEmptyString() {
        Sound sound = TEST_SOUND.toBuilder()
                                .images(null)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_ifNoWaveFormat_returnEmptyString() {
        Sound sound = TEST_SOUND.toBuilder()
                                .images(Sound.Image.builder().build())
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_emitsSoundMediumWaveformUrl() {
        soundItemViewModel.thumbnailImageUrl()
                          .test()
                          .assertValue(TEST_SOUND.images().medSizeWaveformUrl());
    }

    @Test
    public void name_emitsSoundName() {
        soundItemViewModel.name()
                          .test()
                          .assertValue(TEST_SOUND.name());
    }

    @Test
    public void description_emitsSoundDescription() {
        soundItemViewModel.description()
                          .test()
                          .assertValue(TEST_SOUND.description());
    }

    @Test
    public void duration_roundsUp_fromPoint4() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(0.4f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void duration_roundsUp_fromPoint5() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(2.6f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.duration()
          .test()
          .assertValue(3);
    }

    @Test
    public void duration_roundingUp_doesNotAffectWholeValues() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(1f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void duration_hasMinimumOf1Second() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(0f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void progressPercentange_isAudioPlayerProgressPercentage_whenSoundActiveInPlayer_andPositionNonZero() {
        long positionMs = TimeUnit.SECONDS.toMillis(10);
        float durationSec = 200f;
        int expectedPercentage = 5;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(
                                                                                      sound.id()),
                                                                                      sound.url()))))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentange_expectedPercentage_0() {
        long positionMs = 0;
        float durationSec = 200f;
        int expectedPercentage = 0;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(
                                                                                      sound.id()),
                                                                                      sound.url()))))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentange_expectedPercentage_99Point9() {
        long positionMs = 999;
        float durationSec = 1;
        int expectedPercentage = 99;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(
                                                                                      sound.id()),
                                                                                      sound.url()))))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentange_expectedPercentage_100() {
        long positionMs = 1000;
        float durationSec = 1;
        int expectedPercentage = 100;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(
                                                                                      sound.id()),
                                                                                      sound.url()))))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_LimitedTo100() {
        long positionMs = 2000;
        float durationSec = 1;
        int expectedPercentage = 100;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(
                                                                                      sound.id()),
                                                                                      sound.url()))))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_isNone_whenDifferentSoundActiveInPlayer_usingId() {
        long id1 = 1L;
        long id2 = 2L;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(id1)
                                .url("url")
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(PlayerState.create(PlayerState.State.PLAYING,
                                                         Option.ofObj(PlaybackSource
                                                                              .create(Id.from(id2),
                                                                                      sound.url()))));
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer);

        vm.progressPercentage()
          .test()
          .assertValue(Option.none());
    }

    @Test
    public void openDetails_openSoundViaNavigator() {
        soundItemViewModel.openDetails();

        verify(navigator).openSoundDetails(eq(TEST_SOUND));
    }

    private class ArrangeBuilder {

        private BehaviorSubject<PlayerState> playerStateOnceAndStream =
                BehaviorSubject.createDefault(PlayerState.create(PlayerState.State.IDLE,
                                                                 Option.none()));
        private BehaviorSubject<Long> playerProgressOnceAndStream = BehaviorSubject
                .createDefault(0L);

        ArrangeBuilder() {
            when(audioPlayer.getPlayerStateOnceAndStream()).thenReturn(playerStateOnceAndStream);
            when(audioPlayer.getTimePositionMsOnceAndStream())
                    .thenReturn(playerProgressOnceAndStream);
        }

        ArrangeBuilder withPlayerStateEvent(PlayerState playerState) {
            playerStateOnceAndStream.onNext(playerState);
            return this;
        }

        ArrangeBuilder withPlayerProgressEvent(Long progress) {
            playerProgressOnceAndStream.onNext(progress);
            return this;
        }
    }

}
