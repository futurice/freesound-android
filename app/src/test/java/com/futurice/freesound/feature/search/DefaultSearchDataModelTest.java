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

import androidx.annotation.NonNull;

import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.test.data.TestData;
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultSearchDataModelTest {

    private static final String QUERY = "trains";

    @Mock
    private FreeSoundApiService freeSoundApiService;

    private DefaultSearchDataModel defaultSearchDataModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultSearchDataModel = new DefaultSearchDataModel(freeSoundApiService,
                new TrampolineSchedulerProvider());
    }

    @Test
    public void querySearch_queriesFreesoundSearchService() {
        new Arrangement().withDummySearchResult();

        defaultSearchDataModel.querySearch(QUERY, Completable.complete()).test();

        verify(freeSoundApiService).search(eq(QUERY));
    }

    @Test
    public void querySearch_completes_whenQuerySearchSuccessful() {
        new Arrangement().withSearchResultsFor(QUERY, dummyResults());

        defaultSearchDataModel.querySearch(QUERY, Completable.complete())
                .test()
                .assertComplete();
    }

    @Test
    public void querySearch_doesNotEmitError_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError(new Exception());

        defaultSearchDataModel.querySearch("should-error", Completable.complete())
                .test()
                .assertComplete();
    }

    @Test
    public void getSearchStateOnceAndStream_isInitiallyClear() {
        defaultSearchDataModel.getSearchStateOnceAndStream()
                .test()
                .assertNotTerminated()
                .assertValue(KSearchState.Cleared.INSTANCE);
    }

    @Test
    public void querySearch_triggersSearchStateProgress() {
        new Arrangement().withDummySearchResult();
        TestObserver<KSearchState> ts = defaultSearchDataModel.getSearchStateOnceAndStream()
                .skip(1)
                .test();

        defaultSearchDataModel.querySearch(QUERY, Completable.complete()).subscribe();

        ts.assertValueCount(3);
        assertThat(ts.values().get(0)).isEqualTo(new KSearchState.InProgress(null));
        assertThat(ts.values().get(2)).isInstanceOf(KSearchState.Success.class);
    }

    @Test
    public void getSearchStateOnceAndStream_emitsResults_whenQuerySearch() {
        SoundSearchResult expected = dummyResults();
        new Arrangement().withSearchResultsFor(QUERY, expected);


        defaultSearchDataModel.querySearch(QUERY, Completable.complete()).subscribe();

        defaultSearchDataModel.getSearchStateOnceAndStream()
                .test()
                .assertNoErrors()
                .assertValue(new KSearchState.Success(expected.getResults()));
    }

    @Test
    public void getSearchStateOnceAndStream_doesNotCompleteOrError_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError(new Exception());
        TestObserver<KSearchState> ts = defaultSearchDataModel.getSearchStateOnceAndStream()
                .test();

        defaultSearchDataModel.querySearch("should-error", Completable.complete()).subscribe();

        ts.assertNotTerminated();
    }

    @Test
    public void getSearchStateOnceAndStream_hasNoTerminalEvent() {
        defaultSearchDataModel.getSearchStateOnceAndStream()
                .test()
                .assertNotTerminated();
    }

    @Test
    public void getSearchStateOnceAndStream_emitsErrorValue_whenQuerySearchErrors() {
        Exception searchError = new Exception();
        new Arrangement().withSearchResultError(searchError)
                .act()
                .querySearch();

        defaultSearchDataModel
                .getSearchStateOnceAndStream().test()
                .assertNotTerminated()
                .assertValueCount(1)
                .assertValue(new KSearchState.Error(searchError));
    }

    @Test
    public void getSearchStateOnceAndStream_doesNotTerminate_whenQuerySearchSuccessful() {
        new Arrangement().withDummySearchResult()
                .act()
                .querySearch();

        defaultSearchDataModel.getSearchStateOnceAndStream()
                .test()
                .assertNotTerminated();
    }

    @Test
    public void getSearchStateOnceAndStream_doesNotTerminate_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError();
        TestObserver<KSearchState> ts = defaultSearchDataModel.getSearchStateOnceAndStream()
                .test();

        defaultSearchDataModel.querySearch(QUERY, Completable.complete()).subscribe();

        ts.assertNotTerminated();
    }

    @Test
    public void getSearchStateOnceAndStream_doesNotEmitDuplicateEvents() {
        new Arrangement().withDummySearchResult();
        TestObserver<KSearchState> ts = defaultSearchDataModel.getSearchStateOnceAndStream()
                .skip(1) // ignore initial value
                .test();

        defaultSearchDataModel.querySearch(QUERY, Completable.never()).subscribe();
        defaultSearchDataModel.querySearch(QUERY, Completable.never()).subscribe();

        ts.assertValueCount(1);
    }

    @Test
    public void clear_clearsSearchState() {
        new Arrangement().withDummySearchResult()
                .act()
                .querySearch();

        defaultSearchDataModel.clear().subscribe();

        defaultSearchDataModel.getSearchStateOnceAndStream()
                .test()
                .assertValueCount(1)
                .assertValue(KSearchState.Cleared.INSTANCE)
                .assertNotTerminated();
    }

    @Test
    public void clear_completes() {
        defaultSearchDataModel.clear()
                .test()
                .assertComplete();
    }

    @NonNull
    private static SoundSearchResult dummyResults() {
        return TestData.searchResult(5);
    }

    private class Arrangement {

        Arrangement withDummySearchResult() {
            when(freeSoundApiService.search(anyString()))
                    .thenReturn(Single.just(dummyResults()));
            return this;
        }

        Arrangement withSearchResultsFor(String query, SoundSearchResult results) {
            when(freeSoundApiService.search(eq(query))).thenReturn(Single.just(results));
            return this;
        }

        Arrangement withSearchResultError(Exception exception) {
            when(freeSoundApiService.search(any())).thenReturn(Single.error(exception));
            return this;
        }

        Arrangement withSearchResultError() {
            return withSearchResultError(new Exception());
        }

        Act act() {
            return new Act();
        }
    }

    private class Act {

        void querySearch(String query) {
            defaultSearchDataModel.querySearch(query, Completable.complete()).subscribe();
        }

        void querySearch() {
            querySearch(QUERY);
        }
    }

}
