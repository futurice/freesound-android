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

package com.futurice.freesound.network.api.model;

import com.futurice.freesound.common.InstantiationForbiddenError;

import android.support.annotation.NonNull;

import java.util.Iterator;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

final class RequestHelper {

    @NonNull
    static <T> String asCommaSeparated(@NonNull final Iterable<T> iterable) {
        checkNotNull(iterable);

        StringBuilder sb = new StringBuilder();
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext()) {
            String value = iter.next().toString();
            sb.append(value);
            if (iter.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private RequestHelper() {
        throw new InstantiationForbiddenError();
    }
}
