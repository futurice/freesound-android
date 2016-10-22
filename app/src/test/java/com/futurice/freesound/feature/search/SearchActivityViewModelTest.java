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
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import io.reactivex.observers.TestObserver;
import polanski.option.Option;

import static org.mockito.Mockito.verify;
import static polanski.option.Option.ofObj;

public class SearchActivityViewModelTest {

    @Mock
    SearchDataModel searchDataModel;

    @Mock
    Analytics analytics;

    private SearchActivityViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModel = new SearchActivityViewModel(searchDataModel, analytics);
    }

    @Test
    public void search_emitsAnalyticsEvent() {
        viewModel.search("Query");

        verify(analytics).log("SearchPressedEvent");
    }

//    @Test
//    public void search_recoversFromSearchErrors() {
//         ArrangeBuilder
//                arrangeBuilder = new SearchFragmentViewModelTest.ArrangeBuilder().withErrorWhenSearching();
//        TestObserver<Option<List<DisplayableItem>>> ts = viewModel.getSoundsStream().test();
//
//        viewModel.search("query");
//
//        ts.assertNoValues();
//
//        arrangeBuilder
//                .withSuccessfulSearchResultStream()
//                .enqueueSearchResults(ofObj(TestData.sounds(10)));
//
//        viewModel.search("query");
//
//        ts.assertValueCount(1);
//    }
//

}
