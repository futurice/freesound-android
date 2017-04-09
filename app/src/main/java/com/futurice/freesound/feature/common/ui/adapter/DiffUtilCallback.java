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

package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.feature.common.DisplayableItem;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.List;

final class DiffUtilCallback extends DiffUtil.Callback {

    @NonNull
    private final List<DisplayableItem> oldItems;

    @NonNull
    private final List<DisplayableItem> newItems;

    @NonNull
    private final ItemComparator comparator;

    DiffUtilCallback(@NonNull final List<DisplayableItem> oldItems,
                     @NonNull final List<DisplayableItem> newItems,
                     @NonNull final ItemComparator comparator) {
        this.oldItems = oldItems;
        this.newItems = newItems;
        this.comparator = comparator;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return comparator.areItemsTheSame(oldItems.get(oldItemPosition),
                                          newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition,
                                      final int newItemPosition) {
        return comparator.areContentsTheSame(oldItems.get(oldItemPosition),
                                             newItems.get(newItemPosition));
    }
}
