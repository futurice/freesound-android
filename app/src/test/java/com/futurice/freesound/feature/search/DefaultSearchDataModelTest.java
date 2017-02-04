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

import com.futurice.freesound.network.api.FreeSoundSearchService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.test.data.TestData;
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import polanski.option.Option;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultSearchDataModelTest {

    private static final String QUERY = "trains";

    @Mock
    private FreeSoundSearchService freeSoundSearchService;

    private DefaultSearchDataModel defaultSearchDataModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        defaultSearchDataModel = new DefaultSearchDataModel(freeSoundSearchService,
                                                            new TrampolineSchedulerProvider());
    }

    @Test
    public void querySearch_queriesFreesoundSearchService() {
        new Arrangement().withDummySearchResult();

        defaultSearchDataModel.querySearch(QUERY).test();

        verify(freeSoundSearchService).search(eq(QUERY));
    }

    @Test
    public void querySearch_completes_whenQuerySearchSuccessful() {
        new Arrangement().withSearchResultsFor(QUERY, dummyResults());

        defaultSearchDataModel.querySearch(QUERY)
                              .test()
                              .assertComplete();
    }

    @Test
    public void querySearch_doesNotEmitError_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError(new Exception());

        defaultSearchDataModel.querySearch("should-error")
                              .test()
                              .assertComplete();
    }

    @Test
    public void getSearchResultsOnceAndStream_hasNoneAsDefault() {
        defaultSearchDataModel.getSearchResultsOnceAndStream()
                              .test()
                              .assertNotTerminated()
                              .assertValue(Option.none());
    }

    @Test
    public void getSearchResultsOnceAndStream_emitsResults_whenQuerySearch() {
        SoundSearchResult expected = dummyResults();
        new Arrangement().withSearchResultsFor(QUERY, expected);

        defaultSearchDataModel.querySearch(QUERY).subscribe();

        defaultSearchDataModel.getSearchResultsOnceAndStream()
                              .test()
                              .assertNoErrors()
                              .assertValue(Option.ofObj(expected.results()));

    }

    @Test
    public void getSearchResultsOnceAndStream_doesNotCompleteOrError_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError(new Exception());
        TestObserver<Option<List<Sound>>> ts = defaultSearchDataModel
                .getSearchResultsOnceAndStream()
                .test();

        defaultSearchDataModel.querySearch("should-error").subscribe();

        ts.assertNotTerminated();
    }

    @Test
    public void getSearchResultsOnceAndStream_hasNoTerminalEvent() {
        defaultSearchDataModel.getSearchResultsOnceAndStream()
                              .test()
                              .assertNotTerminated();
    }

    @Test
    public void getSearchErrorOnceAndStream_hasNoneAsDefault() {
        defaultSearchDataModel.getSearchErrorOnceAndStream()
                              .test()
                              .assertNotTerminated()
                              .assertValue(Option.none());
    }

    @Test
    public void getSearchErrorOnceAndStream_emitsErrorValue_whenQuerySearchErrors() {
        Exception searchError = new Exception();
        new Arrangement().withSearchResultError(searchError)
                         .act()
                         .querySearch();

        defaultSearchDataModel.getSearchErrorOnceAndStream()
                              .test()
                              .assertValue(Option.ofObj(searchError));
    }

    @Test
    public void getSearchErrorOnceAndStream_isCleared_whenQuerySearchSuccessful() {
        new Arrangement().withDummySearchResult()
                         .act()
                         .querySearch();

        defaultSearchDataModel.getSearchErrorOnceAndStream()
                              .test()
                              .assertValue(Option.none());
    }

    @Test
    public void getSearchErrorOnceAndStream_doesNotTerminate_whenQuerySearchSuccessful() {
        new Arrangement().withDummySearchResult()
                         .act()
                         .querySearch();

        defaultSearchDataModel.getSearchErrorOnceAndStream()
                              .test()
                              .assertNotTerminated();
    }

    @Test
    public void getSearchErrorOnceAndStream_doesNotTerminate_whenQuerySearchErrors() {
        new Arrangement().withSearchResultError();
        TestObserver<Option<Throwable>> ts = defaultSearchDataModel.getSearchErrorOnceAndStream()
                                                                   .test();

        defaultSearchDataModel.querySearch(QUERY).subscribe();

        ts.assertNotTerminated();
    }

    @Test
    public void clear_clearsSearchResults() {
        new Arrangement().withDummySearchResult()
                         .act()
                         .querySearch();

        defaultSearchDataModel.clear().subscribe();

        defaultSearchDataModel.getSearchResultsOnceAndStream()
                              .test()
                              .assertValue(Option.none())
                              .assertNotTerminated();
    }

    @Test
    public void clear_clearsSearchErrors() {
        new Arrangement().withDummySearchResult()
                         .act()
                         .querySearch();

        defaultSearchDataModel.clear().subscribe();

        defaultSearchDataModel.getSearchErrorOnceAndStream()
                              .test()
                              .assertValue(Option.none())
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
            when(freeSoundSearchService.search(anyString()))
                    .thenReturn(Single.just(dummyResults()));
            return this;
        }

        Arrangement withSearchResultsFor(String query, SoundSearchResult results) {
            when(freeSoundSearchService.search(eq(query))).thenReturn(Single.just(results));
            return this;
        }

        Arrangement withSearchResultError(Exception exception) {
            when(freeSoundSearchService.search(any())).thenReturn(Single.error(exception));
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
            defaultSearchDataModel.querySearch(query).subscribe();
        }

        void querySearch() {
            querySearch(QUERY);
        }
    }

}
