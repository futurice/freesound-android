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
import com.futurice.freesound.feature.audio.PlaybackSource;
import com.futurice.freesound.feature.audio.PlayerState;
import com.futurice.freesound.feature.audio.State;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.User;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.feature.audio.IdKt.from;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SoundItemViewModelTest {

    private static final Sound TEST_SOUND = TestData.sound(1L);

    @Mock
    private Navigator navigator;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private FreeSoundApiService freeSoundApiService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void userAvatar_isUserMediumAvatar() {
        String username = "username";
        String avatar_m = "avatar_m";
        Sound sound = TEST_SOUND.toBuilder()
                                .username(username)
                                .build();
        User user = TestData.user()
                            .toBuilder()
                            .avatar(TestData.avatar()
                                            .toBuilder()
                                            .medium(avatar_m)
                                            .build())
                            .build();

        new ArrangeBuilder().withUserResponse(username, user);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.userAvatar()
          .test()
          .assertValue(avatar_m);
    }

    @Test
    public void username_isUsersUsername() {
        String username = "username";
        Sound sound = TEST_SOUND.toBuilder()
                                .username(username)
                                .build();
        User user = TestData.user()
                            .toBuilder()
                            .username(username)
                            .build();

        new ArrangeBuilder().withUserResponse(username, user);

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.username()
          .test()
          .assertValue(username);
    }

    @Test
    public void created_isSoundsCreatedDate() {
        Date createdDate = new Date(1000L);
        Sound sound = TEST_SOUND.toBuilder()
                                .created(createdDate)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.createdDate()
          .test()
          .assertValue(DateFormat.getDateInstance().format(createdDate));
    }

    @Test
    public void thumbnailImageUrl_ifNoImages_returnEmptyString() {
        Sound sound = TEST_SOUND.toBuilder()
                                .images(null)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_ifNoWaveFormat_returnEmptyString() {
        Sound sound = TEST_SOUND.toBuilder()
                                .images(Sound.Image.builder().build())
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_emitsSoundMediumWaveformUrl() {
        SoundItemViewModel soundItemViewModel = new SoundItemViewModel(TEST_SOUND,
                                                                       navigator,
                                                                       audioPlayer,
                                                                       freeSoundApiService);

        soundItemViewModel.thumbnailImageUrl()
                          .test()
                          .assertValue(TEST_SOUND.images().medSizeWaveformUrl());
    }

    @Test
    public void name_emitsSoundName() {
        SoundItemViewModel soundItemViewModel = new SoundItemViewModel(TEST_SOUND,
                                                                       navigator,
                                                                       audioPlayer,
                                                                       freeSoundApiService);

        soundItemViewModel.name()
                          .test()
                          .assertValue(TEST_SOUND.name());
    }

    @Test
    public void description_emitsSoundDescription() {
        SoundItemViewModel soundItemViewModel = new SoundItemViewModel(TEST_SOUND,
                                                                       navigator,
                                                                       audioPlayer,
                                                                       freeSoundApiService);

        soundItemViewModel.description()
                          .test()
                          .assertValue(TEST_SOUND.description());
    }

    @Test
    public void duration_roundsUp_fromPoint4() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(0.4f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void duration_roundsUp_fromPoint5() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(2.6f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.duration()
          .test()
          .assertValue(3);
    }

    @Test
    public void duration_roundingUp_doesNotAffectWholeValues() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(1f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void duration_hasMinimumOf1Second() {
        Sound sound = TEST_SOUND.toBuilder()
                                .duration(0f)
                                .build();

        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.duration()
          .test()
          .assertValue(1);
    }

    @Test
    public void progressPercentage_isAudioPlayerProgressPercentage_whenSoundActiveInPlayer_andPositionNonZero() {
        long positionMs = TimeUnit.SECONDS.toMillis(10);
        float durationSec = 200f;
        int expectedPercentage = 5;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_expectedPercentage_0() {
        long positionMs = 0;
        float durationSec = 200f;
        int expectedPercentage = 0;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_expectedPercentage_99Point9() {
        long positionMs = 999;
        float durationSec = 1;
        int expectedPercentage = 99;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_expectedPercentage_100() {
        long positionMs = 1000;
        float durationSec = 1;
        int expectedPercentage = 100;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(1L)
                                .url("url")
                                .duration(durationSec)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

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
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.ofObj(expectedPercentage));
    }

    @Test
    public void progressPercentage_isNone_whenDifferentSoundActiveInPlayer_usingId() {
        long id1 = 1L;
        String url1 = "url";
        long id2 = 2L;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(id1)
                                .url(url1)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(new PlayerState(State.PLAYING,
                                                      Option.ofObj(new PlaybackSource(from(id2),
                                                                                      url1))));
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.none());
    }

    @Test
    public void progressPercentage_isNone_whenDifferentSoundActiveInPlayer_usingUrl() {
        long id1 = 1L;
        String url1 = "url";
        long id2 = 2L;
        Sound sound = TEST_SOUND.toBuilder()
                                .id(id1)
                                .url(url1)
                                .build();
        new ArrangeBuilder()
                .withPlayerStateEvent(new PlayerState(State.PLAYING,
                                                      Option.ofObj(
                                                              new PlaybackSource(from(id2),
                                                                                 url1))));
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator, audioPlayer,
                                                       freeSoundApiService);

        vm.progressPercentage()
          .test()
          .assertValue(Option.none());
    }

    @Test
    public void openDetails_openSoundViaNavigator() {
        new SoundItemViewModel(TEST_SOUND, navigator, audioPlayer, freeSoundApiService)
                .openDetails();

        verify(navigator).openSoundDetails(eq(TEST_SOUND));
    }

    // Helpers

    @NonNull
    private static PlayerState playerStatewithSound(@NonNull final State state,
                                                    @NonNull final Sound sound) {
        return new PlayerState(state,
                               Option.ofObj(
                                       new PlaybackSource(from(sound.id()),
                                                          sound.url())));
    }

    private class ArrangeBuilder {

        private BehaviorSubject<PlayerState> playerStateOnceAndStream =
                BehaviorSubject.createDefault(new PlayerState(State.IDLE,
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

        ArrangeBuilder withUserResponse(String username, User user) {
            when(freeSoundApiService.getUser(eq(username))).thenReturn(Single.just(user));
            return this;
        }
    }

}
