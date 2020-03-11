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

package com.futurice.freesound.feature.search;

import com.google.auto.value.AutoValue;

import androidx.annotation.NonNull;

import polanski.option.Option;

@AutoValue
abstract class SearchQuery {

    @NonNull
    abstract Option<String> query();

    abstract boolean clearEnabled();

    @NonNull
    static SearchQuery create(@NonNull final Option<String> query,
                              final boolean clearEnabled) {
        return new AutoValue_SearchQuery(query, clearEnabled);
    }

}
