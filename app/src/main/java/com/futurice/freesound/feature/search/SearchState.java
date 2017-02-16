package com.futurice.freesound.feature.search;

import com.futurice.freesound.common.utils.Preconditions;

import android.support.annotation.NonNull;

import polanski.option.Option;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class SearchState {

    @NonNull
    static SearchState SEARCH_TRIGGERED() {
        return new SearchState(true);
    }

    @NonNull
    static SearchState SEARCH_CLEARED() {
        return new SearchState(false);
    }

    @NonNull
    static SearchState SEARCH_ERROR(@NonNull Throwable throwable) {
        get(throwable);
        return new SearchState(throwable);
    }

    private final boolean value;
    @NonNull
    private final Option<Throwable> optionalThrowable;

    private SearchState(boolean searchTriggered) {
        this.value = searchTriggered;
        this.optionalThrowable = Option.none();
    }

    private SearchState(@NonNull Throwable throwable) {
        this.value = false;
        this.optionalThrowable = Option.ofObj(throwable);
    }

    @NonNull
    Option<Throwable> getError() {
        return optionalThrowable;
    }

    boolean searchTriggered() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SearchState that = (SearchState) o;

        if (value != that.value) {
            return false;
        }
        return optionalThrowable.equals(that.optionalThrowable);

    }
}
