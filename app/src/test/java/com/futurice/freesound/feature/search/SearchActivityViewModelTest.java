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
import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.test.data.TestData;
import com.futurice.freesound.test.rx.TimeSkipScheduler;
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchActivityViewModelTest {

    private static final String DUMMY_QUERY = "test-query";

    @Mock
    private SearchDataModel searchDataModel;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private Analytics analytics;

    @Captor
    private ArgumentCaptor<String> searchTermCaptor;

    @Captor
    private ArgumentCaptor<Completable> searchDelayCaptor;

    private TrampolineSchedulerProvider schedulerProvider;

    private SearchActivityViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        schedulerProvider = new TrampolineSchedulerProvider();
        viewModel = new SearchActivityViewModel(searchDataModel,
                                                audioPlayer,
                                                analytics,
                                                schedulerProvider);
    }

    @Test
    public void onBind_initializesAudioPlayer() {
        new ArrangeBuilder()
                .act()
                .bind();

        verify(audioPlayer).init();
    }

    @Test
    public void unBind_releasesAudioPlayer() {
        new ArrangeBuilder()
                .act()
                .unbind();

        verify(audioPlayer).release();
    }

    @Test
    public void viewModel_clearsSearchDataModel_afterBind() {
        new ArrangeBuilder().withSuccessfulSearchResultStream()
                            .act()
                            .bind();

        verify(searchDataModel).clear();
    }

    @Test
    public void clear_isDisabled_afterInitialized() {
        new ArrangeBuilder();

        viewModel.isClearEnabledOnceAndStream()
                 .test()
                 .assertValue(false)
                 .assertNotTerminated();
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

        verify(searchDataModel).querySearch(eq(DUMMY_QUERY), any(Completable.class));
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

        verify(searchDataModel).querySearch(eq(DUMMY_QUERY), any(Completable.class));
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
        order.verify(searchDataModel).querySearch(eq(DUMMY_QUERY), any(Completable.class));
        order.verify(searchDataModel).clear();
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

        final TestObserver<Boolean> ts = viewModel.isClearEnabledOnceAndStream()
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
                .enqueueSearchResults(TestData.Companion.sounds(10))
                .act()
                .bind()
                .search();

        verify(searchDataModel, times(2)).querySearch(eq(DUMMY_QUERY), any(Completable.class));
    }

    @Test
    public void search_withEmptyQuery_clearsSearchImmediately_afterNonEmptySearch() {
        TestScheduler testScheduler = new TestScheduler();
        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler)
                .act()
                .bind()
                .search();

        testScheduler.advanceTimeBy(SearchActivityViewModel.SEARCH_DEBOUNCE_TIME_SECONDS,
                                    TimeUnit.SECONDS);
        act.search("");

        verify(searchDataModel, times(2)).clear();
    }

    @Test
    public void search_withNonEmptyQuery_doesNotTriggerImmediately() {
        TestScheduler testScheduler = new TestScheduler();

        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler)
                .act()
                .bind();

        act.search(DUMMY_QUERY);

        verify(searchDataModel).querySearch(searchTermCaptor.capture(),
                                            searchDelayCaptor.capture());

        assertThat(searchTermCaptor.getValue()).isEqualTo(DUMMY_QUERY);
        searchDelayCaptor.getValue().test().assertNotTerminated();
    }

    @Test
    public void search_withNonEmptyQuery_triggersAfterDelay() {
        TestScheduler testScheduler = new TestScheduler();
        Act act = new ArrangeBuilder()
                .withTimeScheduler(testScheduler, SearchActivityViewModel.SEARCH_DEBOUNCE_TAG)
                .act()
                .bind();

        act.search(DUMMY_QUERY);

        verify(searchDataModel).querySearch(searchTermCaptor.capture(),
                                            searchDelayCaptor.capture());

        assertThat(searchTermCaptor.getValue()).isEqualTo(DUMMY_QUERY);
        TestObserver<Void> testObserver = searchDelayCaptor.getValue().test();
        testScheduler.advanceTimeBy(SearchActivityViewModel.SEARCH_DEBOUNCE_TIME_SECONDS,
                                    TimeUnit.SECONDS);
        testObserver.assertComplete();
    }

    private class ArrangeBuilder {

        private final BehaviorSubject<SearchState> searchResultsStream = BehaviorSubject
                .createDefault(SearchState.cleared());

        private final BehaviorSubject<SearchState> mockedSearchResultsStream
                = BehaviorSubject.createDefault(SearchState.cleared());

        ArrangeBuilder() {
            Mockito.when(searchDataModel.getSearchStateOnceAndStream())
                   .thenReturn(searchResultsStream);
            Mockito.when(searchDataModel.clear()).thenReturn(Completable.complete());
            Mockito.when(searchDataModel.querySearch(anyString(), any(Completable.class)))
                   .thenReturn(Completable.complete());
            withSuccessfulSearchResultStream();
            withTimeSkipScheduler();
        }

        ArrangeBuilder withTimeScheduler(Scheduler scheduler, String tag) {
            schedulerProvider.setTimeScheduler(s -> s.endsWith(tag) ? scheduler : null);
            return this;
        }

        ArrangeBuilder withTimeScheduler(Scheduler scheduler) {
            schedulerProvider.setTimeScheduler(__ -> scheduler);
            return this;
        }

        ArrangeBuilder withTimeSkipScheduler() {
            return withTimeScheduler(TimeSkipScheduler.instance());
        }

        ArrangeBuilder withSuccessfulSearchResultStream() {
            when(searchDataModel.getSearchStateOnceAndStream())
                    .thenReturn(mockedSearchResultsStream);
            return this;
        }

        ArrangeBuilder enqueueSearchResults(@NonNull final List<Sound> sounds) {
            mockedSearchResultsStream.onNext(SearchState.success(sounds));
            return this;
        }

        ArrangeBuilder withErrorWhenSearching() {
            when(searchDataModel.querySearch(anyString(), any())).thenReturn(
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

        Act unbind() {
            viewModel.unbind();
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
