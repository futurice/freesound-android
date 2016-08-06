package com.futurice.freesound.feature.home;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.activity.BaseActivityModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
class HomeActivityModule {

    @Provides
    @ActivityScope
    HomeViewModel provideHomeViewModel(Navigator navigator) {
        return new HomeViewModel(navigator);
    }

}
