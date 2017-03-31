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

package com.futurice.freesound.network.api;

import android.support.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.futurice.freesound.common.utils.Preconditions.get;
import static com.futurice.freesound.network.api.ApiConstants.TOKEN_QUERY_PARAM;

/**
 * An {@link Interceptor} which adds the required API token query parameter to the request.
 * This token is the secret identifier of the app, not an authentication token for user, nor the
 * application client id.
 *
 * The naming reflects that used in the Freesound documentation: the token is the client secret.
 */
final class FreeSoundApiInterceptor implements Interceptor {

    @NonNull
    private final String clientSecret;

    @Inject
    FreeSoundApiInterceptor(@NonNull final String clientSecret) {
        this.clientSecret = get(clientSecret);
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Request requestWithToken = chain.request()
                                        .newBuilder()
                                        .url(getUrlWithApiToken(chain))
                                        .build();

        return chain.proceed(requestWithToken);
    }

    @NonNull
    private HttpUrl getUrlWithApiToken(final Chain chain) {
        return chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter(TOKEN_QUERY_PARAM,
                                       clientSecret)
                    .build();
    }
}
