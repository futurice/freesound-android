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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.futurice.freesound.network.api.FreeSoundApi;
import com.futurice.freesound.network.api.model.GeoLocation;
import com.futurice.freesound.network.api.model.mapping.GeoLocationDeserializer;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.lang.annotation.Retention;
import java.util.List;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(includes = {ConfigModule.class, InstrumentationModule.class})
public final class ApiModule {

    static final String URL_CONFIG = "ApiModule.URL_CONFIG";
    static final String API_TOKEN_CONFIG = "ApiModule.API_TOKEN_CONFIG";

    @Provides
    @Singleton
    static FreeSoundApi provideFreeSoundApi(@Named(URL_CONFIG) String url,
                                            @ForFreeSoundApi Gson gson,
                                            @ForFreeSoundApi OkHttpClient client) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl(url)
                .build()
                .create(FreeSoundApi.class);
    }

    // Internal //

    @Provides
    @Singleton
    @ForFreeSoundApi
    static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(GeoLocation.class, new GeoLocationDeserializer())
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
    }

    @Provides
    @Singleton
    @ForFreeSoundApi
    static OkHttpClient provideApiOkHttpClient(@AppInterceptors List<Interceptor> appInterceptor,
                                               @NetworkInterceptors List<Interceptor> networkInterceptor) {
        return createOkHttpClient(appInterceptor, networkInterceptor);
    }

    private static OkHttpClient createOkHttpClient(List<Interceptor> appInterceptors,
                                                   List<Interceptor> networkInterceptors) {
        Builder okBuilder = new Builder();
        okBuilder.interceptors().addAll(appInterceptors);
        okBuilder.networkInterceptors().addAll(networkInterceptors);

        return okBuilder.build();
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
