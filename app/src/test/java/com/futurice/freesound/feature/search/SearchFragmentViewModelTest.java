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

import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.feature.common.DisplayableItem.Type.SOUND;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static polanski.option.Option.ofObj;

public class SearchFragmentViewModelTest {

    @Mock
    SearchDataModel searchDataModel;

    @Mock
    Navigator navigator;

    private SearchFragmentViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModel = new SearchFragmentViewModel(searchDataModel, navigator);
    }

    @Test
    public void getSounds_emitsNone_whenSearchResultsIsNone() {
        new ArrangeBuilder().enqueueSearchResults(Option.none());

        TestObserver<Option<List<DisplayableItem>>> ts = viewModel.getSoundsStream().test();

        ts.assertValue(Option.none());
    }

    @Test
    public void getSounds_emitsSearchResultsWrappedInDisplayableItems() {
        List<Sound> sounds = TestData.sounds(10);
        new ArrangeBuilder().enqueueSearchResults(ofObj(sounds));

        TestObserver<Option<List<DisplayableItem>>> ts = viewModel.getSoundsStream().test();

        ts.assertValue(ofObj(expectedDisplayableItems(sounds)));
    }


    @NonNull
    private static List<DisplayableItem> expectedDisplayableItems(
            @NonNull final List<Sound> sounds) {
        List<DisplayableItem> displayableItems = new ArrayList<>();
        for (Sound sound : sounds) {
            displayableItems.add(DisplayableItem.create(sound, SOUND));
        }
        return displayableItems;
    }

    private class ArrangeBuilder {

        private final BehaviorSubject<Option<List<Sound>>> mockedSearchResultsStream
                = BehaviorSubject.create();

        ArrangeBuilder() {
            withSuccessfulSearchResultStream();
        }

        ArrangeBuilder withSuccessfulSearchResultStream() {
            when(searchDataModel.getSearchResultsOnceAndStream())
                    .thenReturn(mockedSearchResultsStream);
            return this;
        }

        ArrangeBuilder enqueueSearchResults(@NonNull final Option<List<Sound>> sounds) {
            mockedSearchResultsStream.onNext(sounds);
            return this;
        }

        ArrangeBuilder withErrorWhenSearching() {
            when(searchDataModel.querySearch(anyString())).thenReturn(
                    Completable.error(new Exception()));
            return this;
        }
    }
}
