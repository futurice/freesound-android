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
import com.futurice.freesound.utils.ExceptionHelper;

import android.support.annotation.NonNull;

import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Common function implementations.
 */
public final class Functions {

    private static final Action NOTHING0 = () -> {
    };

    private static final Consumer NOTHING1 = __ -> {
    };

    /**
     * Returns an instance of {@link Action} with no side-effects.
     *
     * @return the {@link Action}
     */
    @NonNull
    public static Action nothing0() {
        //noinspection unchecked
        return NOTHING0;
    }

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

    /**
     * Wraps the call to the function in try-catch and propagates thrown
     * checked exceptions as RuntimeException.
     *
     * @param <T> the first input type
     * @param <U> the second input type
     * @param <R> the output type
     * @param f   the function to call, not null (not verified)
     * @param t   the first parameter value to the function
     * @param u   the second parameter value to the function
     * @return the result of the function call
     */
    public static <T, U, R> R apply(BiFunction<T, U, R> f, T t, U u) {
        try {
            return f.apply(t, u);
        } catch (Exception ex) {
            throw ExceptionHelper.wrapOrThrow(ex);
        }
    }

    private Functions() {
        throw new InstantiationForbiddenError();
    }
}
