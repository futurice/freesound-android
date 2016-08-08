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

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import static com.futurice.freesound.utils.Preconditions.get;

/**
 * Refer to: http://www.freesound.org/docs/api/resources_apiv2.html#sound-resources
 */
@AutoValue
public abstract class Sound implements Parcelable {

    // The sound’s unique identifier.
    @Nullable
    public abstract Long id();

    // The URI for this sound on the Freesound website.
    @Nullable
    public abstract String url();

    // The name user gave to the sound.
    @Nullable
    public abstract String name();

    // An array of tags the user gave to the sound.
    @Nullable
    public abstract List<String> tags();

    // The description the user gave to the sound.
    @Nullable
    public abstract String description();

    // Latitude and longitude of the geotag separated by spaces
    // (e.g. “41.0082325664 28.9731252193”, only for sounds that have been geotagged).
    @Nullable
    public abstract GeoLocation geotag();

    // The username of the uploader of the sound.
    @Nullable
    public abstract String username();

    // Dictionary including the URIs for spectrogram and waveform visualizations of the sound.
    // The dictionary includes the fields waveform_l and waveform_m (for large and medium waveform
    // images respectively), and spectral_l and spectral_m (for large and medium spectrogram images
    // respectively).
    @Nullable
    public abstract Map<SoundImageFormat, String> images();

    // Duration in seconds
    @Nullable
    public abstract Float duration();

    @NonNull
    public static TypeAdapter<Sound> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Sound.GsonTypeAdapter(get(gson));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(@NonNull final Long id);

        public abstract Builder url(@NonNull final String url);

        public abstract Builder name(@NonNull final String name);

        public abstract Builder tags(@NonNull final List<String> tags);

        public abstract Builder description(@NonNull final String description);

        public abstract Builder geotag(@NonNull final GeoLocation geoLocation);

        public abstract Builder username(@NonNull final String username);

        public abstract Builder images(@NonNull final Map<SoundImageFormat, String> images);

        public abstract Builder duration(@NonNull final Float duration);

        @NonNull
        public abstract Sound build();
    }

    @NonNull
    public static Sound.Builder builder() {
        return new AutoValue_Sound.Builder();
    }

}
