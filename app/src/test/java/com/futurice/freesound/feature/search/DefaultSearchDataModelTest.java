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
import com.futurice.freesound.test.rx.IgnoringCompletableObserver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import polanski.option.Option;

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
        defaultSearchDataModel = new DefaultSearchDataModel(freeSoundSearchService);
    }

    @Test
    public void querySearch_queriesSearchDataModelWithTerm() {
        new Arrangement()
                .withSearchResultsFor(QUERY, dummyResults());

        defaultSearchDataModel.querySearch(QUERY).test();

        verify(freeSoundSearchService).search(eq(QUERY));
    }

    @Test
    public void querySearch_emitsSingleUnit_whenQuerySearchSuccessful() {
        new Arrangement()
                .withSearchResultsFor(QUERY, dummyResults());

        TestSubscriber<Void> ts = defaultSearchDataModel.querySearch(QUERY).test();

        ts.assertNoErrors()
          .assertComplete();
    }

    @Test
    public void querySearch_emitsError_whenQuerySearchErrors() {
        final Exception expected = new Exception();
        new Arrangement().withSearchResultError(expected);

        TestSubscriber<Void> ts = defaultSearchDataModel.querySearch("query").test();

        ts.assertError(expected);
    }

    @Test
    public void querySearch_returnsResults_whenQuerySearch_withEmptyTerm() {
        final SoundSearchResult result = dummyResults();
        new Arrangement()
                .withSearchResultsFor("", result);
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        defaultSearchDataModel.querySearch("").subscribe();

        ts.assertNoErrors()
          .assertValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_returnsResults_whenQuerySearch() {
        final SoundSearchResult result = dummyResults();
        new Arrangement()
                .withSearchResultsFor(QUERY, result);
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        defaultSearchDataModel.querySearch(QUERY).subscribe();

        ts.assertNoErrors()
          .assertValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_doesNotError_whenQuerySearchErrors() {
        new Arrangement()
                .withSearchResultError(new Exception());
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        defaultSearchDataModel.querySearch("dummy").subscribe(IgnoringCompletableObserver.create());

        ts.assertNoErrors();
    }

    @Test
    public void getSearchResults_hasNoDefaultResults() {
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        ts.assertNoErrors()
          .assertNoValues();
    }

    @Test
    public void getSearchResults_hasNoTerminalEvent() {
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        ts.assertNotTerminated();
    }

    @Test
    public void getSearchResults_cachesLastResult() {
        final SoundSearchResult result = dummyResults();
        new Arrangement()
                .withSearchResultsFor(QUERY, result)
                .act().querySearch(QUERY);

        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        ts.assertNoErrors()
          .assertValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_emitsEmptyList_whenCleared() {
        TestObserver<Option<List<Sound>>> ts =
                defaultSearchDataModel.getSearchResultsStream().test();

        defaultSearchDataModel.clear().subscribe();

        ts.assertNoErrors()
          .assertValue(Option.none());
    }

    @Test
    public void clear_emitsSingleUnit() {
        TestSubscriber<Void> ts = defaultSearchDataModel.clear().test();

        ts.assertNoErrors()
          .assertComplete();
    }

    @NonNull
    private static SoundSearchResult dummyResults() {
        return TestData.searchResult(5);
    }

    private class Arrangement {

        Arrangement withSearchResultsFor(String query, SoundSearchResult results) {
            when(freeSoundSearchService.search(eq(query))).thenReturn(Single.just(results));
            return this;
        }

        Arrangement withSearchResultError(Exception exception) {
            when(freeSoundSearchService.search(any())).thenReturn(Single.error(exception));
            return this;
        }

        Act act() {
            return new Act();
        }
    }

    private class Act {

        void querySearch(String query) {
            defaultSearchDataModel.querySearch(query).subscribe();
        }
    }

}
