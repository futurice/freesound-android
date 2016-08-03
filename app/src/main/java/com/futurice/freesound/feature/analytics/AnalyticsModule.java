package com.futurice.freesound.feature.analytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    public Analytics provideAnalytics(FirebaseAnalytics firebaseAnalytics) {
        return firebaseAnalytics;
    }
}
