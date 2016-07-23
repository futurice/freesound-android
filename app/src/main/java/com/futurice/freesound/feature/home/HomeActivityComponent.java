package com.futurice.freesound.feature.home;

import com.futurice.freesound.app.FreesoundApplicationComponent;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.activity.BaseActivityComponent;
import com.futurice.freesound.inject.activity.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = FreesoundApplicationComponent.class,
        modules = HomeActivityModule.class)
public interface HomeActivityComponent extends BaseActivityComponent {

    HomeViewModel getHomeViewModel();

    Navigator getNavigator();

    void inject(final HomeActivity activity);
}


