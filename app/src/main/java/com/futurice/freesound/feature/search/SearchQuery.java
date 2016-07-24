package com.futurice.freesound.feature.search;

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

@AutoValue
abstract class SearchQuery {

    @NonNull
    abstract String query();

    abstract boolean clearEnabled();

    @NonNull
    static SearchQuery create(@NonNull final String query,
                              final boolean clearEnabled) {
        return new AutoValue_SearchQuery(query, clearEnabled);
    }

}
