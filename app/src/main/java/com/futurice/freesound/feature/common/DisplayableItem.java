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

package com.futurice.freesound.feature.common;

import com.google.auto.value.AutoValue;

import android.support.annotation.NonNull;

/**
 * Wraps a model. Convenient for complex presentation layers such as
 * {@link android.support.v7.widget.RecyclerView} where different types of model are handled.
 */
@AutoValue
public abstract class DisplayableItem<T> {

    @NonNull
    public abstract int type();

    @NonNull
    public abstract T model();

    @SuppressWarnings("NullableProblems")
    @AutoValue.Builder
    public interface Builder<T> {

        @NonNull
        Builder<T> type(@NonNull int type);

        @NonNull
        Builder<T> model(@NonNull T model);

        @NonNull
        DisplayableItem<T> build();
    }

    @NonNull
    public static <T> Builder<T> builder() {
        return new AutoValue_DisplayableItem.Builder<>();
    }

    @NonNull
    public static DisplayableItem create(@NonNull final Object model, final int type) {
        return DisplayableItem.builder().type(type).model(model).build();
    }
}
