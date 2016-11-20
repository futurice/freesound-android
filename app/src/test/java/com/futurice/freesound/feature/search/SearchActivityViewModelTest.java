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
import com.futurice.freesound.rx.TimeScheduler;
import com.futurice.freesound.rx.TimeSkipScheduler;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.feature.search.SearchActivityViewModel.SEARCH_DEBOUNCE_TAG;
import static com.futurice.freesound.feature.search.SearchActivityViewModel.SEARCH_DEBOUNCE_TIME_SECONDS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static polanski.option.Option.ofObj;

public class SearchActivityViewModelTest {

    private static final String DUMMY_QUERY = "test-query";

    @Mock
    private SearchDataModel searchDataModel;

    @Mock
    private Analytics analytics;

    private SearchActivityViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        viewModel = new SearchActivityViewModel(searchDataModel, analytics);
    }

    @Test
    public void viewModel_clearsSearchDataModel_byDefault() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        verify(searchDataModel).clear();
    }

    @Test
    public void search_emitsAnalyticsEvent() {
        viewModel.search(DUMMY_QUERY);

        verify(analytics).log("SearchPressedEvent");
    }

    @Test
    public void search_queriesSearchDataModelWithTerm() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(DUMMY_QUERY);

        verify(searchDataModel).querySearch(eq(DUMMY_QUERY));

    }

    @Test
    public void search_duplicateNonEmptyQueriesAreIgnored() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(DUMMY_QUERY);
        viewModel.search(DUMMY_QUERY);
        viewModel.search(DUMMY_QUERY);
        viewModel.search(DUMMY_QUERY);
        viewModel.search(DUMMY_QUERY);

        verify(searchDataModel).querySearch(DUMMY_QUERY);
    }

    @Test
    public void search_duplicateEmptyQueriesAreIgnored() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search("");
        viewModel.search("");
        viewModel.search("");
        viewModel.search("");
        viewModel.search("");

        verify(searchDataModel).clear();
    }

    @Test
    public void search_clearsSearchDataModel_whenEmptySearchString_afterSearchWithNonEmptyString() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(DUMMY_QUERY);
        viewModel.search("");

        InOrder order = inOrder(searchDataModel);
        order.verify(searchDataModel).clear();
        order.verify(searchDataModel).querySearch(DUMMY_QUERY);
        order.verify(searchDataModel).clear();
    }

    @Test
    public void clear_isDisabled_byDefault() {
        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(false)
                 .assertNotTerminated();
    }

    @Test
    public void clear_isEnabled_whenSearchWithNonEmptyQuery() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(DUMMY_QUERY);

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(true)
                 .assertNotTerminated();
    }

    @Test
    public void clear_isDisabled_whenSearchWithEmptyQuery_afterSearchWithNonEmptyString() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();
        final TestObserver<Boolean> ts = viewModel
                .isClearEnabledOnceAndStream()
                .test();

        viewModel.search(DUMMY_QUERY);
        viewModel.search("");

        ts.assertValues(false, true, false)
          .assertNotTerminated();
    }

    @Test
    public void search_recoversFromSearchErrors_callsApiAgain() {
        ArrangeBuilder arrangement = new ArrangeBuilder();
        arrangement.withErrorWhenSearching()
                   .act()
                   .bind()
                   .search();

        arrangement
                .withSuccessfulSearchResultStream()
                .enqueueSearchResults(ofObj(TestData.sounds(10)))
                .act()
                .bind()
                .search();

        verify(searchDataModel, times(2)).querySearch(eq(DUMMY_QUERY));
    }

    @Test
    public void search_withEmptyQuery_clearsSearchImmediately_afterNonEmptySearch() {
        TestScheduler testScheduler = new TestScheduler();
        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler)
                .act()
                .bind()
                .search();

        testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_TIME_SECONDS, TimeUnit.SECONDS);
        act.search("");

        verify(searchDataModel, times(2)).clear();
    }

    @Test
    public void search_withNonEmptyQuery_doesNotTriggerImmediately() {
        Act act = new ArrangeBuilder()
                .withTimeScheduler(new TestScheduler())
                .act()
                .bind();

        act.search(DUMMY_QUERY);

        verify(searchDataModel, never()).querySearch(eq(DUMMY_QUERY));
    }

    @Test
    public void search_withNonEmptyQuery_triggersAfterDelay() {
        TestScheduler testScheduler = new TestScheduler();
        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler, SEARCH_DEBOUNCE_TAG)
                .act()
                .bind();

        act.search(DUMMY_QUERY);
        testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_TIME_SECONDS, TimeUnit.SECONDS);

        verify(searchDataModel).querySearch(DUMMY_QUERY);
    }

    private class ArrangeBuilder {

        private final BehaviorSubject<Option<List<Sound>>> searchResultsStream = BehaviorSubject
                .createDefault(Option.none());

        private final BehaviorSubject<Option<List<Sound>>> mockedSearchResultsStream
                = BehaviorSubject.createDefault(Option.none());

        ArrangeBuilder() {
            Mockito.when(searchDataModel.getSearchResultsOnceAndStream())
                   .thenReturn(searchResultsStream);
            Mockito.when(searchDataModel.clear()).thenReturn(Completable.complete());
            Mockito.when(searchDataModel.querySearch(anyString()))
                   .thenReturn(Completable.complete());
            withSuccessfulSearchResultStream();
            withTimeSkipScheduler();
        }

        ArrangeBuilder withTimeScheduler(Scheduler scheduler, String tag) {
            TimeScheduler.setTimeSchedulerHandler((s, t) ->
                                                          t.endsWith(tag)
                                                                  ? scheduler
                                                                  : TimeSkipScheduler.instance());
            return this;
        }

        ArrangeBuilder withTimeScheduler(Scheduler scheduler) {
            TimeScheduler.setTimeSchedulerHandler((s, __) -> scheduler);
            return this;
        }

        ArrangeBuilder withTimeSkipScheduler() {
            return withTimeScheduler(TimeSkipScheduler.instance());
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

        Act act() {
            return new Act();
        }
    }

    private class Act {

        Act bind() {
            viewModel.bindToDataModel();
            return this;
        }

        Act search() {
            viewModel.search(DUMMY_QUERY);
            return search(DUMMY_QUERY);
        }

        Act search(String query) {
            viewModel.search(query);
            return this;
        }
    }

}
