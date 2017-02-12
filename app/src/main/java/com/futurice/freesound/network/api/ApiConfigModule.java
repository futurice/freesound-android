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

import com.futurice.freesound.BuildConfig;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApiConfigModule {

    static final String API_URL_CONFIG = "ApiConfigModule.API_URL_CONFIG";
    static final String API_CLIENT_SECRET_CONFIG = "ApiConfigModule.API_CLIENT_ID_SECRET_CONFIG";
    static final String API_CLIENT_ID_CONFIG = "ApiConfigModule.API_CLIENT_ID_CONFIG";

    @Provides
    @Singleton
    @Named(API_URL_CONFIG)
    static String provideApiModuleUrlConfig() {
        return BuildConfig.FREESOUND_API_URL;
    }

    @Provides
    @Singleton
    @Named(API_CLIENT_SECRET_CONFIG)
    static String provideApiModuleApiClientSecretConfig() {
        return BuildConfig.FREESOUND_API_CLIENT_SECRET;
    }

    @Provides
    @Singleton
    @Named(API_CLIENT_ID_CONFIG)
    static String provideApiModuleApiClientIdConfig() {
        return BuildConfig.FREESOUND_API_CLIENT_ID;
    }

}
