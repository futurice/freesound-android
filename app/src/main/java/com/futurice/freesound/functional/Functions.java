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

package com.futurice.freesound.functional;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.NonNull;

import io.reactivex.functions.Consumer;

/**
 * Common function implementations.
 */
public final class Functions {

    private static final Consumer NOTHING1 = __ -> {
    };

    /**
     * Returns an instance of {@link Consumer} with no side-effects.
     *
     * @return the {@link Consumer}
     */
    @NonNull
    public static <T> Consumer<T> nothing1() {
        //noinspection unchecked
        return NOTHING1;
    }

    private Functions() {
        throw new InstantiationForbiddenError();
    }
}
