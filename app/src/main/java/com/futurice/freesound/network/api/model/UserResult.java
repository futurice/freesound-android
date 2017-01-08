package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.support.annotation.NonNull;

import static com.futurice.freesound.common.utils.Preconditions.get;

@AutoValue
public abstract class UserResult {

    @NonNull
    public abstract String username();

    @NonNull
    public abstract String about();

    @NonNull
    public abstract AvatarResult avatar();

    @NonNull
    public static TypeAdapter<UserResult> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_UserResult.GsonTypeAdapter(get(gson));
    }

    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public interface Builder {

        @NonNull
        Builder username(@NonNull final String username);

        @NonNull
        Builder about(@NonNull final String about);

        @NonNull
        Builder avatar(@NonNull final AvatarResult avatar);

        @NonNull
        UserResult build();
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_UserResult.Builder();
    }
}
