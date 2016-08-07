package com.futurice.freesound.ui.adapter.base;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.Collection;

import polanski.option.Option;

/**
 * Helper interface that aids an adapter with holding the model.
 */
public interface DefaultAdapterInteractor<T> {

    /**
     * Updates the current existing list of items with the given one.
     *
     * @param items Items to be used to change the collection
     * @return {@code true} if the adapter has been changed
     */
    boolean update(@NonNull final Collection<T> items);

    /**
     * Appends new items to currently existing ones.
     *
     * @param items collection of items that should be added to the list
     * @return {@code true} if the adapter has been changed
     */
    boolean append(@NonNull final Collection<T> items);

    /**
     * Removes an item at the specified position.
     *
     * @param position of the item to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean remove(@IntRange(from = 0) final int position);

    /**
     * Removes specific item.
     *
     * @param item to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean remove(@NonNull final T item);

    /**
     * Removes a collection of items.
     *
     * @param items to be removed
     * @return {@code true} if the adapter has been changed
     */
    boolean removeAll(@NonNull final Collection<T> items);

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
    Option<T> getItem(@IntRange(from = 0) final int position);

    /**
     * Returns an option of the index where the item exists.
     *
     * @param item of item to be found
     * @return Option of index of item or {@link Option#NONE} if wasn't found
     */
    @NonNull
    Option<Integer> getItemPosition(@NonNull final T item);

    /**
     * Inserts an item at the specified position.
     *
     * @param item     the item to insert
     * @param position the position the item needs to be
     */
    boolean insert(final T item, final int position);
}
