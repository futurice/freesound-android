package com.futurice.freesound.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class Preconditions {

    public static <T> void checkNotNull(@Nullable final T value) {
        checkNotNull(value, "Value cannot be null");
    }

    public static <T> void checkNotNull(@Nullable final T value, @NonNull final String msg) {
        if (value == null) {
            throw new NullPointerException(msg);
        }
    }

    @NonNull
    public static <T> T get(T value) {
        checkNotNull(value);
        return value;
    }

    private Preconditions() {
        throw new AssertionError("No instances allowed");
    }
}
