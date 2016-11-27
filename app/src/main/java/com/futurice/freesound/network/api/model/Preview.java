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

package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.futurice.freesound.utils.Preconditions.get;

@AutoValue
public abstract class Preview implements Parcelable {

    @Nullable
    @SerializedName("preview-lq-mp3")
    public abstract String lowQualityMp3Url();

    @Nullable
    @SerializedName("preview-hq-mp3")
    public abstract String highQualityMp3Url();

    @Nullable
    @SerializedName("preview-lq-ogg")
    public abstract String lowQualityOggUrl();

    @Nullable
    @SerializedName("preview-hq-ogg")
    public abstract String highQualityOggUrl();

    @NonNull
    public static TypeAdapter<Preview> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Preview.GsonTypeAdapter(get(gson));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder lowQualityMp3Url(@NonNull final String url);

        public abstract Builder highQualityMp3Url(@NonNull final String url);

        public abstract Builder lowQualityOggUrl(@NonNull final String url);

        public abstract Builder highQualityOggUrl(@NonNull final String url);

        @NonNull
        public abstract Preview build();
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Preview.Builder();
    }
}
