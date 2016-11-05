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
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.feature.search.SearchActivityViewModel.SEARCH_DEBOUNCE_TAG;
import static com.futurice.freesound.feature.search.SearchActivityViewModel.SEARCH_DEBOUNCE_TIME_SECONDS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static polanski.option.Option.ofObj;

public class SearchActivityViewModelTest {

    private static final String QUERY = "test-query";

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
    public void search_emitsAnalyticsEvent() {
        viewModel.search(QUERY);

        verify(analytics).log("SearchPressedEvent");
    }

    @Ignore("This will require Scheduler overriding")
    @Test
    public void querySearch_queriesSearchDataModelWithTerm() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(QUERY);

        verify(searchDataModel).querySearch(eq(QUERY));
    }

    @Ignore("This will require Scheduler overriding")
    @Test
    public void querySearch_clearsSearchDataModel_whenEmptySearchString() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search("");

        verify(searchDataModel).clear();
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
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search(QUERY);

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(true)
                 .assertNotTerminated();
    }

    @Ignore(("This will require Scheduler overriding"))
    @Test
    public void clear_isDisabled_whenSearchWithEmptyQuery() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        viewModel.search("");

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(false)
                 .assertNotTerminated();
    }

    @Ignore(("This will require Scheduler overriding"))
    @Test
    public void search_recoversFromSearchErrors() {
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

        verify(searchDataModel).querySearch(eq(QUERY));
    }

    @Test
    public void emptyQuery_clearsSearchImmediately() {
        Act act = new ArrangeBuilder()
                .act()
                .bind();

        act.search("");

        verify(searchDataModel).clear();
    }

    @Test
    public void nonEmptyQuery_doesNotTriggerImmediately() {
        Act act = new ArrangeBuilder()
                .withTimeScheduler(new TestScheduler())
                .act()
                .bind();

        act.search("Something");

        verify(searchDataModel, never()).querySearch(eq("Something"));
    }

    @Test
    public void nonEmptyQuery_triggersAfterDelay() {
        TestScheduler testScheduler = new TestScheduler();
        String query = "Something";
        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler, SEARCH_DEBOUNCE_TAG)
                .act()
                .bind();

        act.search(query);
        testScheduler.advanceTimeBy(SEARCH_DEBOUNCE_TIME_SECONDS, TimeUnit.SECONDS);

        verify(searchDataModel).querySearch(query);
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
            viewModel.search(QUERY);
            return search(QUERY);
        }

        Act search(String query) {
            viewModel.search(query);
            return this;
        }
    }

}
