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

public interface ItemComparator {

    /**
     * Decides whether two {@link DisplayableItem} represent the same Item.
     * For example, if your items have unique ids, this method should check their id equality.
     *
     * @return True if the two items represent the same object or false if they are different.
     */
    boolean areItemsTheSame(final DisplayableItem item1, final DisplayableItem item2);

    /**
     * Checks whether the visual representation of two {@link DisplayableItem}s are the same.
     *
     * This method is called only if {@link #areItemsTheSame(DisplayableItem, DisplayableItem)}
     * returns {@code true} for these items. For instance, when the item is the same with different
     * state, like selected.
     *
     * @return True if the visual representation for the {@link DisplayableItem}s are the same or
     * false if they are different.
     */
    boolean areContentsTheSame(final DisplayableItem item1, final DisplayableItem item2);
}
