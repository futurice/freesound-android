package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import static com.futurice.freesound.common.utils.Preconditions.get;

@AutoValue
public abstract class Avatar {

    @NonNull
    public abstract String small();

    @NonNull
    public abstract String medium();

    @NonNull
    public abstract String large();

    @NonNull
    public static TypeAdapter<Avatar> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Avatar.GsonTypeAdapter(get(gson));
    }

    @VisibleForTesting
    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public interface Builder {

        @NonNull
        Builder small(@NonNull final String small);

        @NonNull
        Builder medium(@NonNull final String medium);

        @NonNull
        Builder large(@NonNull final String large);

        @NonNull
        Avatar build();
    }

    @VisibleForTesting
    @NonNull
    public static Builder builder() {
        return new AutoValue_Avatar.Builder();
    }
}
