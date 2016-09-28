package com.futurice.freesound.viewmodel;

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

/**
 * Wraps a model. Convenient for complex presentation layers such as
 * {@link android.support.v7.widget.RecyclerView} where different types of model are handled.
 */
@AutoValue
public abstract class DisplayableItem<T> {

    // List types
    public enum Type {
        SOUND,
        AD // this is just for demo only
    }

    public abstract Type type();

    @NonNull
    public abstract T model();

    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public interface Builder<T> {

        @NonNull
        Builder<T> type(@NonNull Type type);

        @NonNull
        Builder<T> model(@NonNull T model);

        @NonNull
        DisplayableItem<T> build();
    }

    @NonNull
    public static <T> Builder<T> builder() {
        return new AutoValue_DisplayableItem.Builder<>();
    }

    @NonNull
    public static DisplayableItem create(@NonNull final Object model, final Type type) {
        return DisplayableItem.builder().type(type).model(model).build();
    }
}
