package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;

import android.support.annotation.Nullable;

import java.util.List;

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
}
