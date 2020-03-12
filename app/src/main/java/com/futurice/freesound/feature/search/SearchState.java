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

import androidx.annotation.NonNull;

import com.futurice.freesound.network.api.model.Sound;
import com.google.auto.value.AutoValue;

import java.util.List;

import polanski.option.Option;

@AutoValue
abstract class SearchState {

    @NonNull
    abstract Option<List<Sound>> results();

    @NonNull
    abstract Option<Throwable> error();

    abstract boolean isInProgress();

    @NonNull
    static SearchState create(Option<List<Sound>> results, Option<Throwable> error,
                              boolean isInProgress) {
        return new AutoValue_SearchState(results, error, isInProgress);
    }

//    @NonNull
//    static SearchState cleared() {
//        return new AutoValue_SearchState(Option.none(), Option.none(), false);
//    }
//
//    @NonNull
//    static SearchState error(@NonNull Throwable throwable) {
//        return new AutoValue_SearchState(Option.none(), Option.ofObj(throwable), false);
//    }
//
//    @NonNull
//    static SearchState success(@NonNull List<Sound> results) {
//        return new AutoValue_SearchState(Option.ofObj(results), Option.none(), false);
//    }
}
