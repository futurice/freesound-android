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

package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.futurice.freesound.common.utils.Preconditions.get;

@AutoValue
public abstract class AccessToken {

    @Nullable
    public abstract String accessToken();

    @Nullable
    public abstract String scope();

    @Nullable
    public abstract Long expiresIn();

    @Nullable
    public abstract String refreshToken();

    @NonNull
    public static TypeAdapter<AccessToken> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_AccessToken.GsonTypeAdapter(get(gson));
    }

}
