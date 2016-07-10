package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static com.futurice.freesound.utils.Preconditions.get;

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

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder count(@NonNull final Integer count);

        public abstract Builder next(@Nullable final String next);

        public abstract Builder results(@NonNull final List<Sound> results);

        public abstract Builder previous(@Nullable final String previous);

        @NonNull
        public abstract SoundSearchResult build();
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_SoundSearchResult.Builder();
    }
}
