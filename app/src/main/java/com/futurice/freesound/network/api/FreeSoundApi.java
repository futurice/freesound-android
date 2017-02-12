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

import com.futurice.freesound.network.api.model.AccessToken;
import com.futurice.freesound.network.api.model.SoundFields;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.network.api.model.User;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit2 annotated interface to the Freesound API.
 *
 * Refer to {@see https://www.freesound.org/docs/api/}.
 */
interface FreeSoundApi {

    @NonNull
    @GET("/apiv2/search/text/")
    Single<SoundSearchResult> search(@Query("query") @NonNull String query,
                                     @Query("filter") @Nullable String filter,
                                     @Query("fields") @NonNull SoundFields fields);

    @NonNull
    @GET("/apiv2/users/{user}/")
    Single<User> user(@Path("user") @NonNull final String user);

    @NonNull
    @POST("/apiv2/oauth2/access_token/")
    Single<AccessToken> accessToken(@Header("client_id") @NonNull String clientId,
                                    @Header("client_secret") @NonNull String clientSecret,
                                    @Header("grant_type") @NonNull String grantType,
                                    @Header("code") @NonNull String code);

}
