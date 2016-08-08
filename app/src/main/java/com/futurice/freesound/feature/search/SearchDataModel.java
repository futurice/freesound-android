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

import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.model.Sound;

import android.support.annotation.NonNull;

import java.util.List;

import polanski.option.Option;
import rx.Observable;

public interface SearchDataModel {

    @NonNull
    Observable<Unit> querySearch(@NonNull String query);

    @NonNull
    Observable<Option<List<Sound>>> getSearchResults();

    @NonNull
    Observable<Unit> clear();
}
