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
import com.futurice.freesound.common.utils.ExceptionHelper;

import androidx.annotation.NonNull;

import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Common function implementations.
 */
public final class Functions {

    /**
     * Returns an instance of {@link Action} with no side-effects.
     *
     * @return the {@link Action}
     */
    @NonNull
    public static Action nothing0() {
        return () -> {
        };
    }

    /**
     * Returns an instance of {@link Consumer} with no side-effects.
     *
     * @return the {@link Consumer}
     */
    @NonNull
    public static <T> Consumer<T> nothing1() {
        return v -> {
        };
    }

    /**
     * A {@link Predicate} which always returns true.
     *
     * @param <T> the predicate type.
     * @return a {@link Predicate} which ignores the value and returns {@link Boolean#TRUE}.
     */
    @NonNull
    public static <T> Predicate<T> alwaysTrue() {
        return v -> true;
    }

    /**
     * A {@link Predicate} which always returns false.
     *
     * @param <T> the predicate type.
     * @return a {@link Predicate} which ignores the value and returns {@link Boolean#FALSE}.
     */
    @NonNull
    public static <T> Predicate<T> alwaysFalse() {
        return v -> false;
    }

    /**
     * Identity function - returns parameter as result.
     *
     * @param <T> function argument and result type.
     * @return the identity value of the provided type.
     */
    @NonNull
    public static <T> Function<T, T> identity() {
        return t -> t;
    }

    /**
     * Inverting function - inverts the supplied {@link Boolean} value.
     */
    public static Function<Boolean, Boolean> invert() {
        return v -> !v;
    }

    /**
     * Filter predicate function - allows only {@link Boolean#TRUE} values to pass.
     */
    @NonNull
    public static Function<Boolean, Boolean> ifTrue() {
        return v -> v;
    }

    /**
     * Filter predicate function - allows only {@link Boolean#FALSE} values to pass.
     */
    @NonNull
    public static Function<Boolean, Boolean> ifFalse() {
        return v -> !v;
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
