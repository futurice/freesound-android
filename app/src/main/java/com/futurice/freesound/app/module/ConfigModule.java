package com.futurice.freesound.app.module;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ConfigModule {

    @Provides
    @Singleton @Named(ApiModule.URL_CONFIG)
    String provideApiModuleUrlConfig() {
        return "https://www.freesound.org/";
    }

    @Provides
    @Singleton @Named(ApiModule.API_TOKEN_CONFIG)
    String provideApiModuleApiTokenConfig() {
        throw new IllegalStateException("No Api Token defined");
    }

}
