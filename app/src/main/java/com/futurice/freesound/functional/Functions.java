package com.futurice.freesound.functional;

import android.support.annotation.NonNull;

import rx.functions.Action1;

public final class Functions {

    @NonNull
    public static <T> Action1<T> nothing1() {
        return __ -> {
        };
    }

    private Functions() {
        throw new AssertionError("No instances allowed");
    }
}
