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

import com.futurice.freesound.network.api.model.AccessToken;
import com.futurice.freesound.network.api.model.SoundFields;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.network.api.model.User;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class DefaultFreeSoundApiService implements FreeSoundApiService {

    @NonNull
    private final FreeSoundApi freeSoundApi;

    @NonNull
    private final String clientId;

    @NonNull
    private final String clientSecret;

    @Inject
    DefaultFreeSoundApiService(@NonNull final FreeSoundApi freeSoundApi,
                               @Named(ApiConfigModule.API_CLIENT_ID_CONFIG) @NonNull final String clientId,
                               @Named(ApiConfigModule.API_CLIENT_SECRET_CONFIG) @NonNull final String clientSecret) {
        this.freeSoundApi = get(freeSoundApi);
        this.clientId = get(clientId);
        this.clientSecret = get(clientSecret);
    }

    @Override
    @NonNull
    public Single<User> getUser(@NonNull final String user) {
        return freeSoundApi.user(user);
    }

    @Override
    @NonNull
    public Single<AccessToken> getAccessToken(@NonNull final String code) {
        return freeSoundApi.accessToken(clientId,
                                        clientSecret,
                                        ApiConstants.AUTHORIZATION_CODE_GRANT_TYPE_VALUE,
                                        code);
    }

    @Override
    @NonNull
    public Single<SoundSearchResult> search(@NonNull final String query) {
        return freeSoundApi.search(get(query), null, SoundFields.BASE);
    }

}
