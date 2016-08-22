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

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;
import com.futurice.freesound.viewmodel.DisplayableItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import polanski.option.Option;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;

import static com.futurice.freesound.test.utils.TestSubscriberUtils.testSubscribe;
import static com.futurice.freesound.viewmodel.DisplayableItem.SOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class SearchViewModelTest {

    @Mock
    SearchDataModel searchDataModel;

    @Mock
    Navigator navigator;

    @Mock
    Analytics analytics;

    private SearchViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModel = new SearchViewModel(searchDataModel, navigator, analytics);
    }

    @Test
    public void search_emitsAnalyticsEvent() {
        viewModel.search("Query");

        verify(analytics).log("SearchPressedEvent");
    }

    @Test
    public void getSounds_emitsNone_whenSearchResultsIsNone() {
        new ArrangeBuilder().enqueueSearchResults(Option.none());

        TestSubscriber<Option<List<DisplayableItem>>> ts = testSubscribe(viewModel.getSounds());

        ts.assertValue(Option.none());
    }

    @Test
    public void getSounds_emitsSearchResultsWrappedInDisplayableItems() {
        List<Sound> sounds = TestData.sounds(10);
        new ArrangeBuilder().enqueueSearchResults(Option.ofObj(sounds));

        TestSubscriber<Option<List<DisplayableItem>>> ts = testSubscribe(viewModel.getSounds());

        ts.assertValueCount(1);
        Option<List<DisplayableItem>> displayables = ts.getOnNextEvents().get(0);
        assertThat(displayables.isSome()).isTrue();
        assertDisplayableItems(displayables.orDefault(ArrayList::new), sounds);
    }

    private static void assertDisplayableItems(@NonNull final List<DisplayableItem> displayables,
                                               @NonNull final List<Sound> sounds) {
        assertThat(displayables.size()).isEqualTo(sounds.size());
        for (int i = 0; i < displayables.size(); i++) {
            assertThat(displayables.get(i).type()).isEqualTo(SOUND);
            assertThat(displayables.get(i).model()).isEqualTo(sounds.get(i));
        }
    }

    private class ArrangeBuilder {

        private final BehaviorSubject<Option<List<Sound>>> searchResultsStream = BehaviorSubject
                .create();

        ArrangeBuilder() {
            Mockito.when(searchDataModel.getSearchResults()).thenReturn(searchResultsStream);
        }

        ArrangeBuilder enqueueSearchResults(@NonNull final Option<List<Sound>> sounds) {
            searchResultsStream.onNext(sounds);
            return this;
        }
    }
}
