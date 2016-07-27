package com.futurice.freesound.feature.search;

import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.FreeSoundSearchService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import java.util.List;

import polanski.option.Option;
import rx.Observable;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;

import static com.futurice.freesound.test.utils.TestSubscriberUtils.testSubscribe;
import static com.petertackage.assertrx.Assertions.assertThat;
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

        testSubscribe(defaultSearchDataModel.querySearch(QUERY));

        verify(freeSoundSearchService).search(eq(QUERY));
    }

    @Test
    public void querySearch_emitsSingleUnit_whenQuerySearchSuccessful() {
        new Arrangement()
                .withSearchResultsFor(QUERY, dummyResults());

        TestSubscriber<Unit> ts = testSubscribe(defaultSearchDataModel.querySearch(QUERY));

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Unit.DEFAULT)
                      .hasCompleted();
    }

    @Test
    public void querySearch_emitsError_whenQuerySearchErrors() {
        final Exception expected = new Exception();
        new Arrangement().withSearchResultError(expected);

        TestSubscriber<Unit> ts = testSubscribe(defaultSearchDataModel.querySearch("query"));

        assertThat(ts).hasError(expected);
    }

    @Test
    public void querySearch_returnsResults_whenQuerySearch_withEmptyTerm() {
        final SoundSearchResult result = dummyResults();

        new Arrangement()
                .withSearchResultsFor("", result);

        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        testSubscribe(defaultSearchDataModel.querySearch(""));

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_returnsResults_whenQuerySearch() {
        final SoundSearchResult result = dummyResults();
        new Arrangement()
                .withSearchResultsFor(QUERY, result);
        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        testSubscribe(defaultSearchDataModel.querySearch(QUERY));

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_doesNotError_whenQuerySearchErrors() {
        new Arrangement()
                .withSearchResultError(new Exception());
        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        defaultSearchDataModel.querySearch("dummy").subscribe(new TestObserver<>());

        assertThat(ts).hasNoErrors();
    }

    @Test
    public void getSearchResults_hasNoDefaultResults() {
        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        assertThat(ts).hasNoErrors()
                      .hasNoValues();
    }

    @Test
    public void getSearchResults_hasNoTerminalEvent() {
        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        assertThat(ts).hasNoTerminalEvent();
    }

    @Test
    public void getSearchResults_cachesLastResult() {
        final SoundSearchResult result = dummyResults();
        new Arrangement()
                .withSearchResultsFor(QUERY, result)
                .act().querySearch(QUERY);

        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Option.ofObj(result.results()));
    }

    @Test
    public void getSearchResults_emitsEmptyList_whenCleared() {
        TestSubscriber<Option<List<Sound>>> ts = testSubscribe(
                defaultSearchDataModel.getSearchResults());

        testSubscribe(defaultSearchDataModel.clear());

        assertThat(ts).hasNoErrors()
                      .hasReceivedValueWhich().isEqualTo(Option.none());
    }

    @Test
    public void clear_emitsSingleUnit() {
        TestSubscriber<Unit> ts = testSubscribe(defaultSearchDataModel.clear());

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Unit.DEFAULT)
                      .hasCompleted();
    }

    @NonNull
    private static SoundSearchResult dummyResults() {
        return TestData.searchResult(5);
    }

    private class Arrangement {

        Arrangement withSearchResultsFor(String query, SoundSearchResult results) {
            when(freeSoundSearchService.search(eq(query))).thenReturn(Observable.just(results));
            return this;
        }

        Arrangement withSearchResultError(Exception exception) {
            when(freeSoundSearchService.search(any())).thenReturn(Observable.error(exception));
            return this;
        }

        Act act() {
            return new Act();
        }
    }

    private class Act {

        void querySearch(String query) {
            defaultSearchDataModel.querySearch(query).subscribe(new TestObserver<>());
        }
    }

}
