/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.utils;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.Nullable;

/**
 * String utilities - matches those in the Android platform, but separated to allow for JVM unit
 * testing.
 */
public final class TextUtils {

    /**
     * Evaluates if the given sequence is either null or empty.
     *
     * @return true if the parameter is null or empty, false otherwise.
     */
    public static boolean isNullOrEmpty(@Nullable final CharSequence chars) {
        return chars == null || chars.length() == 0;
    }

    /**
     * Evaluates if the given sequence is neither null nor empty.
     *
     * @return true if the parameter neither null nor empty, false otherwise.
     */
    public static boolean isNotNullOrEmpty(@Nullable final CharSequence chars) {
        return !isNullOrEmpty(chars);
    }

    private TextUtils() {
        throw new InstantiationForbiddenError();
    }
}
