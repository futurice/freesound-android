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
import com.futurice.freesound.utils.TextUtils;

import android.support.annotation.NonNull;

import io.reactivex.functions.Predicate;

public final class StringFunctions {

    @NonNull
    public static Predicate<String> isEmpty() {
        return TextUtils::isNullOrEmpty;
    }

    @NonNull
    public static Predicate<String> isNotEmpty() {
        return TextUtils::isNotNullOrEmpty;
    }

    private StringFunctions() {
        throw new InstantiationForbiddenError();
    }
}
