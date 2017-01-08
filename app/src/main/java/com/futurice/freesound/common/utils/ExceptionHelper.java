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
package com.futurice.freesound.common.utils;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.NonNull;

/**
 * Utility class for exceptions.
 */
public final class ExceptionHelper {

    /**
     * If the provided Throwable is an Error this method
     * throws it, otherwise returns a RuntimeException wrapping the error
     * if that error is a checked exception.
     *
     * @param error the error to wrap or throw
     * @return the (wrapped) error
     */
    @NonNull
    public static RuntimeException wrapOrThrow(@NonNull final Throwable error) {
        if (error instanceof Error) {
            throw (Error) error;
        }
        if (error instanceof RuntimeException) {
            return (RuntimeException) error;
        }
        return new RuntimeException(error);
    }

    private ExceptionHelper() {
        throw new InstantiationForbiddenError();
    }

}
