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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.List;

import static com.futurice.freesound.common.utils.Preconditions.get;

@AutoValue
public abstract class SoundSearchResult {

    @NonNull
    public abstract Integer count();

    @Nullable
    public abstract String next();

    @NonNull
    public abstract List<Sound> results();

    @Nullable
    public abstract String previous();

    @NonNull
    public static TypeAdapter<SoundSearchResult> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_SoundSearchResult.GsonTypeAdapter(get(gson));
    }

    @VisibleForTesting
    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder count(@NonNull final Integer count);

        public abstract Builder next(@Nullable final String next);

        public abstract Builder results(@NonNull final List<Sound> results);

        public abstract Builder previous(@Nullable final String previous);

        @NonNull
        public abstract SoundSearchResult build();
    }

    @VisibleForTesting
    @NonNull
    public static Builder builder() {
        return new AutoValue_SoundSearchResult.Builder();
    }
}
