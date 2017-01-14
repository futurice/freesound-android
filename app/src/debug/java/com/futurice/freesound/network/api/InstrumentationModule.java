/*
 * Copyright 2017 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.network.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module
public final class InstrumentationModule {

    @Provides
    @Singleton
    @ApiModule.NetworkInterceptors
    static List<Interceptor> provideNetworkInterceptors(HttpLoggingInterceptor loggingInterceptor,
                                                        StethoInterceptor stethoInterceptor) {
        List<Interceptor> networkInterceptors = new ArrayList<>(2);
        networkInterceptors.add(loggingInterceptor);
        networkInterceptors.add(stethoInterceptor);
        return networkInterceptors;
    }

    @Provides
    @Singleton
    @ApiModule.AppInterceptors
    static List<Interceptor> provideAppInterceptors() {
        return Collections.emptyList();
    }

    @Provides
    @Singleton
    static HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                message -> Timber.tag("OkHttp").d(message));
        // TODO Headers only: Bug in HttpLoggingInterceptor, uses not public api
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return interceptor;
    }

    @Provides
    @Singleton
    static StethoInterceptor provideStethoInterceptor() {
        return new StethoInterceptor();
    }
}
