package com.futurice.freesound.app;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.inject.app.BaseApplicationComponent;
import com.futurice.freesound.inject.app.ForApplication;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.squareup.picasso.Picasso;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = FreesoundApplicationModule.class)
@Singleton
public interface FreesoundApplicationComponent extends BaseApplicationComponent {

    android.app.Application getApplication();

    @ForApplication
    Context getApplicationContext();

    FreeSoundApi getApi();

    Picasso getPicasso();

    Analytics getAnalytics();

    void inject(final FreesoundApplication application);
}
