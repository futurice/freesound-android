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

package com.futurice.freesound.core.adapter;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.Collection;

import polanski.option.Option;

/**
 * Helper interface that aids an adapter with holding the model.
 */
public interface AdapterInteractor<T> {

    /**
     * Updates the current existing list of items with the given one.
     *
     * @param items Items to be used to change the collection
     * @return {@code true} if the adapter has been changed
     */
    boolean update(@NonNull Collection<T> items);

    /**
     * Appends new items to currently existing ones.
     *
     * @param items collection of items that should be added to the list
     * @return {@code true} if the adapter has been changed
     */
    boolean append(@NonNull Collection<T> items);

    /**
     * Removes an item at the specified position.
     *
     * @param position of the item to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean remove(@IntRange(from = 0) int position);

    /**
     * Removes specific item.
     *
     * @param item to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean remove(@NonNull T item);

    /**
     * Removes a collection of items.
     *
     * @param items to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean removeAll(@NonNull Collection<T> items);

    /**
     * Removes all the current items.
     *
     * @return {@code true} if the adapter has been changed
     */
    boolean reset();

    /**
     * Current count of items in the adapter.
     */
    int getCount();

    /**
     * Returns an option of the model object at the position.
     *
     * @param position of the item
     * @return option of the model object at the position.
     */
    @NonNull
    Option<T> getItem(@IntRange(from = 0) int position);

    /**
     * Returns an option of the index where the item exists.
     *
     * @param item of item to be found
     * @return Option of index of item or {@link Option#NONE} if wasn't found
     */
    @NonNull
    Option<Integer> getItemPosition(@NonNull T item);

    /**
     * Inserts an item at the specified position.
     *
     * @param item     the item to insert
     * @param position the position the item needs to be
     */
    boolean insert(@NonNull T item, int position);
}
