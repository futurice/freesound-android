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
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static polanski.option.Option.ofObj;

public class SearchActivityViewModelTest {

    private static final String QUERY = "test-query";

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

    @Ignore("This will require Scheduler overriding")
    @Test
    public void querySearch_queriesSearchDataModelWithTerm() {
        new Arrangement().withSuccessfulSearchResultStream()
                         .act()
                         .subscribed();

        viewModel.search(QUERY);

        verify(searchDataModel).querySearch(eq(QUERY));
    }

    @Ignore("This will require Scheduler overriding")
    @Test
    public void querySearch_clearsSearchDataModel_whenEmptySearchString() {
        fail("Not yet implemented");
    }

    @Ignore(("This will require Scheduler overriding"))
    @Test
    public void clear_isDisabled_byDefault() {
        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(false)
                 .assertNotTerminated();
    }

    @Ignore(("This will require Scheduler overriding"))
    @Test
    public void clear_isEnabled_whenSearchWithNonEmptyQuery() {
        viewModel.search("query");

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(true)
                 .assertNotTerminated();
    }

    @Ignore(("This will require Scheduler overriding"))
    @Test
    public void clear_isDisabled_whenSearchWithEmptyQuery() {
        viewModel.search("");

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(false)
                 .assertNotTerminated();
    }

    @Test
    public void search_recoversFromSearchErrors() {
        Arrangement arrangement = new Arrangement();
        arrangement.withErrorWhenSearching()
                   .act()
                   .search();

        arrangement
                .withSuccessfulSearchResultStream()
                .enqueueSearchResults(ofObj(TestData.sounds(10)));

        viewModel.search(QUERY);

        verify(searchDataModel).querySearch(eq(QUERY));
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

        Arrangement withErrorWhenSearching() {
            when(searchDataModel.querySearch(anyString())).thenReturn(
                    Completable.error(new Exception()));
            return this;
        }

        Act act() {
            return new Act();
        }
    }

    private class Act {

        CompositeDisposable d = new CompositeDisposable();

        void subscribed() {
            viewModel.bind(d);
        }

        void search() {
            viewModel.search(QUERY);
        }
    }

}
