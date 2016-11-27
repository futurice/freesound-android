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
public abstract class Image implements Parcelable {

    @Nullable
    @SerializedName("waveform_m")
    public abstract String medSizeWaveformUrl();

    @Nullable
    @SerializedName("waveform_l")
    public abstract String largeSizeWaveformUrl();

    @Nullable
    @SerializedName("spectral_m")
    public abstract String medSizeSpectralUrl();

    @Nullable
    @SerializedName("spectral_l")
    public abstract String largeSizeSpectralUrl();

    @NonNull
    public static TypeAdapter<Image> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Image.GsonTypeAdapter(get(gson));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder medSizeWaveformUrl(@NonNull final String url);

        public abstract Builder largeSizeWaveformUrl(@NonNull final String url);

        public abstract Builder medSizeSpectralUrl(@NonNull final String url);

        public abstract Builder largeSizeSpectralUrl(@NonNull final String url);

        @NonNull
        public abstract Image build();
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Image.Builder();
    }
}
