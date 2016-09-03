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
import com.futurice.freesound.network.api.FreeSoundApiInterceptor;
import com.futurice.freesound.network.api.model.GeoLocation;
import com.futurice.freesound.network.api.model.mapping.GeoLocationDeserializer;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.lang.annotation.Retention;
import java.util.List;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(includes = {ConfigModule.class, InstrumentationModule.class})
public class ApiModule {

    static final String URL_CONFIG = "ApiModule.URL_CONFIG";
    static final String API_TOKEN_CONFIG = "ApiModule.API_TOKEN_CONFIG";

    @Provides
    @Singleton
    static FreeSoundApi provideFreeSoundApi(Endpoint endpoint,
                                            Client client,
                                            Converter converter,
                                            RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(client)
                .setConverter(converter)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(FreeSoundApi.class);
    }

    // Internal //

    @Provides
    @Singleton
    static Endpoint provideEndpoint(@Named(URL_CONFIG) String url) {
        return Endpoints.newFixedEndpoint(url);
    }

    @Provides
    @Singleton
    static OkHttpClient provideApiOkHttpClient(@AppInterceptors List<Interceptor> appInterceptor,
                                               @NetworkInterceptors List<Interceptor> networkInterceptor) {
        return createOkHttpClient(appInterceptor, networkInterceptor);
    }

    @Provides
    @Singleton
    static Client provideClient(OkHttpClient okHttpClient) {
        return new OkClient(okHttpClient);
    }

    @Provides
    @Singleton
    static Converter provideConverter(Gson gson) {
        return new GsonConverter(gson);
    }

    @Provides
    @Singleton
    static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(GeoLocation.class, new GeoLocationDeserializer())
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
    }

    @Provides
    @Singleton
    static RequestInterceptor provideRequestInterceptor(@Named(API_TOKEN_CONFIG) String apiToken) {
        return new FreeSoundApiInterceptor(apiToken);
    }

    private static OkHttpClient createOkHttpClient(List<Interceptor> appInterceptors,
                                                   List<Interceptor> networkInterceptors) {
        OkHttpClient client = new OkHttpClient();

        // Install interceptors
        client.interceptors().addAll(appInterceptors);
        client.networkInterceptors().addAll(networkInterceptors);

        return client;
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface AppInterceptors {
    }

    @Qualifier
    @Retention(RUNTIME)
    @interface NetworkInterceptors {
    }

}
