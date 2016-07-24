package com.futurice.freesound.functional;

import com.futurice.freesound.utils.TextUtils;

import android.support.annotation.NonNull;

import rx.functions.Func1;

public final class StringFunctions {

    @NonNull
    public static Func1<String, Boolean> isEmpty() {
        return TextUtils::isNullOrEmpty;
    }

    @NonNull
    public static Func1<String, Boolean> isNotEmpty() {
        return TextUtils::isNotNullOrEmpty;
    }

    private StringFunctions() {
        throw new AssertionError("No instances allowed");
    }
}
