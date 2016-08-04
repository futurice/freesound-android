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
