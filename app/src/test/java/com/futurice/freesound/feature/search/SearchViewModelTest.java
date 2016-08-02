package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.common.Navigator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class SearchViewModelTest {

    @Mock
    SearchDataModel searchDataModel;

    @Mock
    Navigator navigator;

    @Mock
    Analytics analytics;

    private SearchViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewModel = new SearchViewModel(searchDataModel, navigator, analytics);
    }

    @Test
    public void search_emitsAnalyticsEvent() {
        viewModel.search("Query");

        verify(analytics).log("SearchPressedEvent");
    }
}
