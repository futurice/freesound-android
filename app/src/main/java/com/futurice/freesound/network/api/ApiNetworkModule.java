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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.network.api.model.GeoLocation;
import com.futurice.freesound.network.api.model.mapping.FreesoundDateAdapter;
import com.futurice.freesound.network.api.model.mapping.GeoLocationJsonAdapter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory;

import java.lang.annotation.Retention;
import java.util.List;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.futurice.freesound.network.api.ApiConfigModule.API_CLIENT_SECRET_CONFIG;
import static com.futurice.freesound.network.api.ApiConfigModule.API_URL_CONFIG;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(includes = {ApiConfigModule.class, InstrumentationModule.class})
public class ApiNetworkModule {

    @Provides
    @Singleton
    static FreeSoundApi provideFreeSoundApi(@Named(API_URL_CONFIG) String url,
                                            @ForFreeSoundApi Moshi moshi,
                                            @ForFreeSoundApi OkHttpClient client,
                                            SchedulerProvider schedulerProvider) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulerProvider.io()))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .baseUrl(url)
                .build()
                .create(FreeSoundApi.class);
    }

    // Internal //

    @Provides
    @Singleton
    @ForFreeSoundApi
    static Moshi provideMoshi() {
        return new Moshi.Builder()
                .add(new GeoLocationJsonAdapter())
                .add(new KotlinJsonAdapterFactory())
                .add(new FreesoundDateAdapter(new Rfc3339DateJsonAdapter()))
                .build();
    }

    @Provides
    @Singleton
    @ForFreeSoundApi
    static OkHttpClient provideApiOkHttpClient(@AppInterceptors List<Interceptor> appInterceptor,
                                               @NetworkInterceptors List<Interceptor> networkInterceptor,
                                               FreeSoundApiInterceptor apiInterceptor) {
        return createOkHttpClient(appInterceptor, networkInterceptor, apiInterceptor);
    }

    private static OkHttpClient createOkHttpClient(List<Interceptor> appInterceptors,
                                                   List<Interceptor> networkInterceptors,
                                                   FreeSoundApiInterceptor freeSoundApiInterceptor) {
        Builder okBuilder = new Builder();
        okBuilder.interceptors().addAll(appInterceptors);
        okBuilder.networkInterceptors().addAll(networkInterceptors);
        okBuilder.interceptors().add(freeSoundApiInterceptor);

        return okBuilder.build();
    }

    @Provides
    @Singleton
    static FreeSoundApiInterceptor provideApiInterceptor(
            @Named(API_CLIENT_SECRET_CONFIG) String clientSecret) {
        return new FreeSoundApiInterceptor(clientSecret);
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface AppInterceptors {
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface NetworkInterceptors {
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface ForFreeSoundApi {
    }

}
