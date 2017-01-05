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
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.feature.common.DisplayableItem.Type.SOUND;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static polanski.option.Option.ofObj;

public class SearchFragmentViewModelTest {

    @Mock
    private SearchDataModel searchDataModel;

    @Mock
    private Navigator navigator;

    @Mock
    private AudioPlayer audioPlayer;

    private SearchFragmentViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        viewModel = new SearchFragmentViewModel(searchDataModel, navigator, audioPlayer);
    }

    @Test
    public void getSounds_emitsNone_whenSearchResultsIsNone() {
        new Arrangement().enqueueSearchResults(Option.none());

        viewModel.getSoundsOnceAndStream()
                 .test()
                 .assertValue(Option.none());
    }

    @Test
    public void getSounds_emitsSearchResultsWrappedInDisplayableItems() {
        List<Sound> sounds = TestData.sounds(10);
        new Arrangement().enqueueSearchResults(ofObj(sounds));

        viewModel.getSoundsOnceAndStream()
                 .test()
                 .assertValue(ofObj(expectedDisplayableItems(sounds)));
    }

    @Test
    public void stopsAudioPlayback_byDefault() {
        new Arrangement();

        viewModel.getSoundsOnceAndStream()
                 .test();

        verify(audioPlayer).stopPlayback();
    }

    @Test
    public void stopsAudioPlayback_whenSearchResultChange() {
        Arrangement arrangement = new Arrangement();
        viewModel.getSoundsOnceAndStream()
                 .test();
        reset(audioPlayer); // is invoked by default, so reset the mock invocation count.

        arrangement.enqueueSearchResults(ofObj(TestData.sounds(10)));

        verify(audioPlayer).stopPlayback();
    }

    @Test
    public void stopPlayback_stopsAudioPlayback() {
        viewModel.stopPlayback();

        verify(audioPlayer).stopPlayback();
    }

    // Helpers

    @NonNull
    private static List<DisplayableItem> expectedDisplayableItems(
            @NonNull final List<Sound> sounds) {
        return Observable.fromIterable(sounds)
                         .map(it -> DisplayableItem.create(it, SOUND))
                         .toList()
                         .blockingGet();
    }

    private class Arrangement {

        private final BehaviorSubject<Option<List<Sound>>> mockedSearchResultsStream
                = BehaviorSubject.createDefault(Option.none());

        Arrangement() {
            withSuccessfulSearchResultStream();
        }

        Arrangement withSuccessfulSearchResultStream() {
            when(searchDataModel.getSearchResultsOnceAndStream())
                    .thenReturn(mockedSearchResultsStream);
            return this;
        }

        Arrangement enqueueSearchResults(@NonNull final Option<List<Sound>> sounds) {
            mockedSearchResultsStream.onNext(sounds);
            return this;
        }

    }

}
