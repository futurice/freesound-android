/*
 * Copyright 2018 Futurice GmbH
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

package com.futurice.freesound.feature.user;

import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.User;
import com.futurice.freesound.store.Store;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    private static final String USER_STORE = "userstore";

    @Provides
    @Singleton
    UserRepository provideUserRepository(FreeSoundApiService freesoundApiService,
                                         @Named(USER_STORE) Store<String, User> userStore) {
        return new UserRepository(freesoundApiService, userStore);
    }

    @Provides
    @Named("userstore")
    Store<String, User> provideUserStore() {
        return new UserStore();
    }
}
