package com.futurice.freesound.app;

import com.futurice.freesound.inject.app.Application;
import com.futurice.freesound.inject.app.BaseApplicationComponent;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.squareup.picasso.Picasso;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = FreesoundApplicationModule.class)
@Singleton
public interface FreesoundApplicationComponent extends BaseApplicationComponent {

    android.app.Application getApplication();

    @Application
    Context getApplicationContext();

    FreeSoundApi getApi();

    Picasso getPicasso();

    void inject(final FreesoundApplication application);
}
