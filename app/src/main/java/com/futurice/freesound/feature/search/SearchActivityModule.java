package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.activity.Activity;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.inject.activity.PerActivity;
import com.futurice.freesound.network.api.FreeSoundApi;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
public class SearchActivityModule {

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
    SearchDataModel provideSearchDataModel(FreeSoundApi freeSoundApi) {
        return new SearchDataModel(freeSoundApi);
    }
}
