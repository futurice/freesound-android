package com.futurice.freesound.app.module;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InstrumentationModule {

    @Provides
    @Singleton
    @ApiModule.NetworkInterceptors
    List<Interceptor> provideNetworkInterceptors() {
        ArrayList<Interceptor> networkInterceptors = new ArrayList<>();
        networkInterceptors.add(new StethoInterceptor());
        return networkInterceptors;
    }

    @Provides
    @Singleton
    @ApiModule.AppInterceptors List<Interceptor> provideAppInterceptors() {
        return Collections.emptyList();
    }
}
