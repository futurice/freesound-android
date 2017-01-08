/*
 * Copyright 2017 Futurice GmbH
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

import android.support.annotation.NonNull;

import polanski.option.Option;
import polanski.option.function.Action0;
import polanski.option.function.Action1;
import polanski.option.function.Func1;

/**
 * Function implementations to be used with the Option library.
 *
 * Very similar to {@link Functions}, but with different function types due to the different
 * implementations used by RxJava 2 and Option.
 */
public final class OptionFunctions {

    /**
     * Returns an instance of {@link Action0} with no side-effects.
     *
     * @return the {@link Action0}
     */
    @NonNull
    public static Action0 nothing0() {
        return () -> {
        };
    }

    /**
     * Returns an instance of {@link Action1} with no side-effects.
     *
     * @return the {@link Action1}
     */
    @NonNull
    public static <T> Action1<T> nothing1() {
        return v -> {
        };
    }

    /**
     * A {@link Func1} Predicate which always returns true.
     *
     * @param <T> the predicate type.
     * @return a {@link Func1} which ignores the value and returns {@link Boolean#TRUE}.
     */
    @NonNull
    public static <T> Func1<T, Boolean> alwaysTrue() {
        return v -> true;
    }

    /**
     * A {@link Func1} Predicate which always returns false.
     *
     * @param <T> the predicate type.
     * @return a {@link Func1} which ignores the value and returns {@link Boolean#FALSE}.
     */
    @NonNull
    public static <T> Func1<T, Boolean> alwaysFalse() {
        return v -> false;
    }

    /**
     * Identity function - returns parameter as result.
     *
     * @param <T> function argument and result type.
     * @return the identity value of the provided type.
     */
    @NonNull
    public static <T> Func1<T, T> identity() {
        return t -> t;
    }

    /**
     * Inverting function - inverts the supplied {@link Boolean} value.
     */
    public static Func1<Boolean, Boolean> invert() {
        return v -> !v;
    }

    /**
     * Filter predicate function - allows only {@link Boolean#TRUE} values to pass.
     */
    @NonNull
    public static Func1<Boolean, Boolean> ifTrue() {
        return v -> v;
    }

    /**
     * Filter predicate function - allows only {@link Boolean#FALSE} values to pass.
     */
    @NonNull
    public static Func1<Boolean, Boolean> ifFalse() {
        return v -> !v;
    }

    /**
     * A function which maps an ignored value to {@link Option#none()}
     *
     * @param <T> the input and output type parameter.
     * @return a {@link Func1} which evaluates to {@link Option#none()}
     */
    @NonNull
    public static <T> Func1<T, Option<T>> toNone() {
        return __ -> Option.none();
    }

    private OptionFunctions() {
        throw new InstantiationForbiddenError();
    }
}
