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
import com.futurice.freesound.test.data.TestData;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SoundItemViewModelTest {

    private static final Sound SOUND = TestData.sound(1L);

    @Mock
    private Navigator navigator;

    private SoundItemViewModel soundItemViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        soundItemViewModel = new SoundItemViewModel(SOUND, navigator);
    }

    @Test
    public void thumbnailImageUrl_ifNoImages_returnEmptyString() {
        Sound sound = mock(Sound.class);
        when(sound.images()).thenReturn(null);
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator);

        TestSubscriber<String> ts = vm.thumbnailImageUrl().test();

        ts.assertNoErrors()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_ifNoWaveFormat_returnEmptyString() {
        Sound sound = mock(Sound.class);
        when(sound.images()).thenReturn(Maps.newHashMap());
        SoundItemViewModel vm = new SoundItemViewModel(sound, navigator);

        TestSubscriber<String> ts = vm.thumbnailImageUrl().test();

        ts.assertNoErrors()
          .assertValue("");
    }

    @Test
    public void thumbnailImageUrl_emitsSoundMediumWaveformUrl() {
        TestSubscriber<String> ts = soundItemViewModel.thumbnailImageUrl().test();

        ts.assertNoErrors()
          .assertValue(SOUND.images().get(SoundImageFormat.waveform_m));
    }

    @Test
    public void name_emitsSoundName() {
        TestSubscriber<String> ts = soundItemViewModel.name().test();

        ts.assertNoErrors()
          .assertValue(SOUND.name());
    }

    @Test
    public void description_emitsSoundDescription() {
        TestSubscriber<String> ts = soundItemViewModel.description().test();

        ts.assertNoErrors()
          .assertValue(SOUND.description());
    }

    @Test
    public void openDetails_openSoundViaNavigator() {
        soundItemViewModel.openDetails();

        verify(navigator).openSoundDetails(eq(SOUND));
    }

}
