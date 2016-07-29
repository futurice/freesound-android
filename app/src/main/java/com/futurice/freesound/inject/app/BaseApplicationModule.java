package com.futurice.freesound.inject.app;

import com.futurice.freesound.feature.common.Analytics;
import com.futurice.freesound.feature.common.DefaultAnalytics;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseApplicationModule {

    private final android.app.Application application;

    public BaseApplicationModule(android.app.Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @Application
    public Context provideApplicationContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public android.app.Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public Analytics provideAnalytics(DefaultAnalytics analytics) {
        return analytics;
    }

}
