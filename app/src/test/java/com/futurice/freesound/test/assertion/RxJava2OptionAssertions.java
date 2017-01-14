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

package com.futurice.freesound.test.assertion;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.NonNull;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import polanski.option.Option;

/**
 * Helpers for testing {@link Option} values using RxJava2's {@link io.reactivex.Observable#test()}
 */
public final class RxJava2OptionAssertions {

    @NonNull
    public static <T> Predicate<Option<T>> isNone() {
        return Option::isNone;
    }

    @NonNull
    public static <T, R> Predicate<T> isNone(@NonNull final Function<T, Option<R>> selector) {
        return v -> selector.apply(v).isNone();
    }

    @NonNull
    public static <T> Predicate<Option<T>> isSome() {
        return Option::isSome;
    }

    @NonNull
    public static <T, R> Predicate<T> isSome(@NonNull final Function<T, Option<R>> selector) {
        return v -> selector.apply(v).isSome();
    }

    @NonNull
    public static <T> Predicate<Option<T>> hasOptionValue(@NonNull final T expected) {
        return t -> t.filter(actual -> actual.equals(expected)).isSome();
    }

    private RxJava2OptionAssertions() {
        throw new InstantiationForbiddenError();
    }
}
