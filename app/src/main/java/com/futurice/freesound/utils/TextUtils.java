package com.futurice.freesound.utils;

import android.support.annotation.Nullable;

public final class TextUtils {

    public static boolean isNullOrEmpty(@Nullable final CharSequence chars) {
        return chars == null || chars.length() == 0;
    }

    public static boolean isNotNullOrEmpty(@Nullable final CharSequence chars) {
        return !isNullOrEmpty(chars);
    }

    private TextUtils() {
        throw new AssertionError("No instances allowed.");
    }
}
