package com.futurice.freesound.feature.search;

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

import polanski.option.Option;

@AutoValue
abstract class SearchQuery {

    @NonNull
    abstract Option<String> query();

    abstract boolean clearEnabled();

    @NonNull
    static SearchQuery create(@NonNull final Option<String> query,
                              final boolean clearEnabled) {
        return new AutoValue_SearchQuery(query, clearEnabled);
    }

}
