/*
 * Copyright 2016 Futurice GmbH
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

package com.futurice.freesound.app.module;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.futurice.freesound.network.api.FreeSoundApiInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

import static com.futurice.freesound.app.module.ApiModule.API_TOKEN_CONFIG;

@Module
public final class InstrumentationModule {

    @Provides
    @Singleton
    @ApiModule.NetworkInterceptors
    static List<Interceptor> provideNetworkInterceptors(FreeSoundApiInterceptor apiInterceptor,
                                                        HttpLoggingInterceptor loggingInterceptor) {
        List<Interceptor> networkInterceptors = new ArrayList<>(1);
        networkInterceptors.add(new StethoInterceptor());
        networkInterceptors.add(apiInterceptor);
        networkInterceptors.add(loggingInterceptor);
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
    static FreeSoundApiInterceptor provideApiInterceptor(@Named(API_TOKEN_CONFIG) String apiToken) {
        return new FreeSoundApiInterceptor(apiToken);
    }

    @Provides
    @Singleton
    static HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                message -> Timber.tag("OkHttp").d(message));
        interceptor
                .setLevel(HttpLoggingInterceptor.Level.HEADERS); // TODO Headers only: Bug in HttpLoggingInterceptor, uses not public api
        return interceptor;
    }
}
