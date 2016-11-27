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

import com.futurice.freesound.feature.audio.MediaSourceFactory;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SoundItemViewModelTest {

    private static final Sound SOUND = TestData.sound(1L);

    @Mock
    private Navigator navigator;

    @Mock
    private SimpleExoPlayer simpleExoPlayer;

    @Mock
    private MediaSourceFactory mediaSourceFactory;

    private SoundItemViewModel soundItemViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        soundItemViewModel = new SoundItemViewModel(SOUND, navigator,
                                                    simpleExoPlayer, mediaSourceFactory);
    }

    @Test
    public void thumbnailImageUrl_ifNoImages_returnEmptyString() {
        Sound sound = mock(Sound.class);
        when(sound.images()).thenReturn(null);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator,
                                                       simpleExoPlayer, mediaSourceFactory);

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_ifNoWaveFormat_returnEmptyString() {
        Sound sound = mock(Sound.class);
<<<<<<< 26353444779dc3bca8ef45cae90e3c23cc459024
        when(sound.images()).thenReturn(Sound.Image.builder().build());
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator);
=======
        when(sound.images()).thenReturn(Maps.newHashMap());
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator,
                                                       simpleExoPlayer, mediaSourceFactory);
>>>>>>> First working audio

        vm.thumbnailImageUrl()
          .test()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_emitsSoundMediumWaveformUrl() {
        soundItemViewModel.thumbnailImageUrl()
                          .test()
                          .assertValue(SOUND.images().medSizeWaveformUrl());
    }

    @Test
    public void name_emitsSoundName() {
        soundItemViewModel.name()
                          .test()
                          .assertValue(SOUND.name());
    }

    @Test
    public void description_emitsSoundDescription() {
        soundItemViewModel.description()
                          .test()
                          .assertValue(SOUND.description());
    }

    @Test
    public void openDetails_openSoundViaNavigator() {
        soundItemViewModel.openDetails();

        verify(navigator).openSoundDetails(eq(SOUND));
    }

}
