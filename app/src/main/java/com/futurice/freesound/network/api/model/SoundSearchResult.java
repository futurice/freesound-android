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

    @Nullable
    public abstract Integer count();

    @Nullable
    public abstract String next();

    @Nullable
    public abstract List<Sound> results();

    @Nullable
    public abstract String previous();

    @NonNull
    public static TypeAdapter<SoundSearchResult> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_SoundSearchResult.GsonTypeAdapter(get(gson));
    }
}
