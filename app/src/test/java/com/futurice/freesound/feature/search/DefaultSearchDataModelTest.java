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

import java.util.List;

import rx.Observable;
import rx.observers.TestObserver;
import rx.observers.TestSubscriber;

import static com.futurice.freesound.test.utils.TestSubscriberUtils.subscribe;
import static com.petertackage.assertrx.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultSearchDataModelTest {

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
        final String query = "trains";
        new Arrangement()
                .withSearchResultsFor(query, TestData.searchResult(5));

        defaultSearchDataModel.querySearch(query).subscribe();

        verify(freeSoundSearchService).search(eq(query));
    }

    @Test
    public void querySearch_returnsResults() {
        final String query = "trains";
        final SoundSearchResult result = TestData.searchResult(5);
        new Arrangement()
                .withSearchResultsFor(query, result);
        TestSubscriber<List<Sound>> ts = subscribe(defaultSearchDataModel.getSearchResults());

        defaultSearchDataModel.querySearch(query).subscribe();

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(result.results());

    }

    @Test
    public void querySearch_emitsSingleUnit() {
        final String query = "trains";
        new Arrangement()
                .withSearchResultsFor("trains", TestData.searchResult(5));

        TestSubscriber<Unit> ts = subscribe(defaultSearchDataModel.querySearch(query));

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(Unit.DEFAULT);
    }

    @Test
    public void querySearch_doesNotError_whenSearchErrors() {
        new Arrangement()
                .withSearchResultError(new Exception());
        TestSubscriber<List<Sound>> ts = subscribe(defaultSearchDataModel.getSearchResults());

        defaultSearchDataModel.querySearch("dummy").subscribe(new TestObserver<>());

        assertThat(ts).hasNoErrors();
    }

    @Test
    public void getSearchResults() {
        fail("Not implemented");
    }

    @Test
    public void clear() {
        fail("Not implemented");
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
    }

}