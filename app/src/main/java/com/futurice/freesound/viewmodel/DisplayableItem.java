package com.futurice.freesound.viewmodel;

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

/**
 * Wraps a model. Convenient for complex presentation layers such as
 * {@link android.support.v7.widget.RecyclerView} where different types of model are handled.
 */
@AutoValue
public abstract class DisplayableItem<T> {

    // Types
    public static final int SOUND = 0;

    public abstract int type();

    @NonNull
    public abstract T model();

    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public abstract static class Builder<T> {

        @NonNull
        public abstract Builder<T> type(@NonNull int type);

        @NonNull
        public abstract Builder<T> model(@NonNull T model);

        @NonNull
        public abstract DisplayableItem<T> build();
    }

    @NonNull
    public static <T> Builder<T> builder() {
        return new AutoValue_DisplayableItem.Builder<>();
    }

    @NonNull
    public static DisplayableItem create(@NonNull final Object model,
                                         final int type) {
        return DisplayableItem.builder().type(type).model(model).build();
    }
}
