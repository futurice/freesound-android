package com.futurice.freesound.app.module;

import com.futurice.freesound.BuildConfig;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigModule {

    @Provides
    @Singleton
    @Named(ApiModule.URL_CONFIG)
    String provideApiModuleUrlConfig() {
        return BuildConfig.FREESOUND_API_URL;
    }

    @Provides
    @Singleton
    @Named(ApiModule.API_TOKEN_CONFIG)
    String provideApiModuleApiTokenConfig() {
        return BuildConfig.FREESOUND_API_KEY;
    }

}
