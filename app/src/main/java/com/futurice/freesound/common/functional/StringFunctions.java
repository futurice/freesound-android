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

package com.futurice.freesound.common.functional;

import com.futurice.freesound.common.InstantiationForbiddenError;
import com.futurice.freesound.common.utils.TextUtils;

import android.support.annotation.NonNull;

import io.reactivex.functions.Predicate;

/**
 * Common String function implementations.
 */
public final class StringFunctions {

    /**
     * Returns a {@link Predicate} which evaluates if a String is null or empty.
     *
     * @return the {@link Predicate}.
     */
    @NonNull
    public static Predicate<String> isEmpty() {
        return TextUtils::isNullOrEmpty;
    }

    /**
     * Returns a {@link Predicate} which evaluates if a String is not null nor empty.
     *
     * @return the {@link Predicate}.
     */
    @NonNull
    public static Predicate<String> isNotEmpty() {
        return TextUtils::isNotEmpty;
    }

    private StringFunctions() {
        throw new InstantiationForbiddenError();
    }
}
