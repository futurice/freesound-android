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

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.futurice.freesound.network.api.ApiConstants.TOKEN_QUERY_PARAM;
import static com.futurice.freesound.utils.Preconditions.get;

public final class FreeSoundApiInterceptor implements Interceptor {

    @NonNull
    private final String apiToken;

    public FreeSoundApiInterceptor(@NonNull final String apiToken) {
        this.apiToken = get(apiToken);
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        HttpUrl url = chain.request()
                           .url()
                           .newBuilder()
                           .addQueryParameter(TOKEN_QUERY_PARAM,
                                              apiToken)
                           .build();
        Request requestWithToken = chain.request()
                                        .newBuilder()
                                        .url(url)
                                        .build();

        return chain.proceed(requestWithToken);
    }
}
