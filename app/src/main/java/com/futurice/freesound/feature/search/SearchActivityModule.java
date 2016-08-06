package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.network.api.DefaultFreeSoundSearchService;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.futurice.freesound.network.api.FreeSoundSearchService;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
class SearchActivityModule {

    @Provides
    @ActivityScope
    SearchViewModel provideSearchViewModel(SearchDataModel searchDataModel,
                                           Navigator navigator,
                                           Analytics analytics) {
        return new SearchViewModel(searchDataModel, navigator, analytics);
    }

    @Provides
    @ActivityScope
    SearchDataModel provideSearchDataModel(FreeSoundSearchService freeSoundSearchService) {
        return new DefaultSearchDataModel(freeSoundSearchService);
    }

    @Provides
    @ActivityScope
    FreeSoundSearchService provideFreeSoundsSearchService(FreeSoundApi freeSoundApi) {
        return new DefaultFreeSoundSearchService(freeSoundApi);
    }
}
