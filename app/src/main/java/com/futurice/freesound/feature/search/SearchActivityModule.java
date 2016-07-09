package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.activity.Activity;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.inject.activity.PerActivity;
import com.futurice.freesound.network.api.DefaultFreeSoundSearchService;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.futurice.freesound.network.api.FreeSoundSearchService;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
class SearchActivityModule {

    @Provides
    @PerActivity
    SearchViewModel provideSearchViewModel(SearchDataModel searchDataModel) {
        return new SearchViewModel(searchDataModel);
    }

    @Provides
    @PerActivity
    Navigator provideNavigator(@Activity Context context) {
        return new Navigator(context);
    }

    @Provides
    @PerActivity
    SearchDataModel provideSearchDataModel(FreeSoundSearchService freeSoundSearchService) {
        return new DefaultSearchDataModel(freeSoundSearchService);
    }

    @Provides
    @PerActivity
    FreeSoundSearchService provideFreeSoundsSearchService(FreeSoundApi freeSoundApi) {
        return new DefaultFreeSoundSearchService(freeSoundApi);
    }
}
