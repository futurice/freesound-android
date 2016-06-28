package com.futurice.freesound;

import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

public enum Unit {
    DEFAULT;

    @NonNull
    public static Unit asUnit(@NonNull final Object obj) {
        checkNotNull(obj);
        return DEFAULT;
    }
}
