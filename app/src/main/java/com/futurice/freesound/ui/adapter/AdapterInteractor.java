package com.futurice.freesound.ui.adapter;

import com.futurice.freesound.ui.adapter.base.IAdapterInteractor;
import com.futurice.freesound.utils.CollectionUtils;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import polanski.option.Option;

import static com.futurice.freesound.utils.Preconditions.checkArgument;
import static polanski.option.Option.ofObj;

/**
 * Handles testable logic for interpreting adapter events.
 */
public class AdapterInteractor<T> implements IAdapterInteractor<T> {

    @NonNull
    protected final List<T> mModels = new ArrayList<>();

    @Override
    public boolean reset() {
        if (!mModels.isEmpty()) {
            mModels.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean update(@NonNull final Collection<T> items) {
        boolean changed = !CollectionUtils.areEqual(mModels, items);

        if (changed) {
            mModels.clear();
            mModels.addAll(items);
        }

        return changed;
    }

    @Override
    public boolean append(@NonNull final Collection<T> items) {
        if (!items.isEmpty()) {
            mModels.addAll(items);
            return true;
        }

        return false;
    }

    @Override
    public boolean insert(@NonNull final T item, @IntRange(from = 0) final int position) {
        assertValidPosition(position);

        mModels.add(position, item);

        return true;
    }

    @Override
    public boolean remove(@IntRange(from = 0) final int position) {
        assertValidPosition(position);

        mModels.remove(position);

        return true;
    }

    @Override
    public boolean remove(@NonNull final T item) {
        return mModels.remove(item);
    }

    @Override
    public boolean removeAll(@NonNull final Collection<T> items) {
        return mModels.removeAll(items);
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @NonNull
    @Override
    public Option<T> getItem(@IntRange(from = 0) final int position) {
        assertValidPosition(position);

        return ofObj(mModels.get(position));
    }

    @NonNull
    @Override
    public Option<Integer> getItemPosition(@NonNull final T item) {
        int position = mModels.indexOf(item);

        return position < 0 ? Option.NONE : ofObj(position);
    }

    private void assertValidPosition(final int position) {
        checkArgument(position < mModels.size(), "Position value is too big.");
    }
}
