package com.futurice.freesound.ui.adapter;

import com.futurice.freesound.ui.adapter.base.DefaultAdapterInteractor;
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
public class AdapterInteractor<T> implements DefaultAdapterInteractor<T> {

    @NonNull
    protected final List<T> models = new ArrayList<>();

    @Override
    public boolean reset() {
        if (models.isEmpty()) {
            return false;
        }
        models.clear();
        return true;
    }

    @Override
    public boolean update(@NonNull final Collection<T> items) {
        boolean changed = !CollectionUtils.areEqual(models, items);
        if (changed) {
            models.clear();
            models.addAll(items);
        }
        return changed;
    }

    @Override
    public boolean append(@NonNull final Collection<T> items) {
        if (!items.isEmpty()) {
            models.addAll(items);
            return true;
        }
        return false;
    }

    @Override
    public boolean insert(@NonNull final T item, @IntRange(from = 0) final int position) {
        assertValidPosition(position);
        models.add(position, item);
        return true;
    }

    @Override
    public boolean remove(@IntRange(from = 0) final int position) {
        assertValidPosition(position);
        models.remove(position);
        return true;
    }

    @Override
    public boolean remove(@NonNull final T item) {
        return models.remove(item);
    }

    @Override
    public boolean removeAll(@NonNull final Collection<T> items) {
        return models.removeAll(items);
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @NonNull
    @Override
    public Option<T> getItem(@IntRange(from = 0) final int position) {
        assertValidPosition(position);
        return ofObj(models.get(position));
    }

    @NonNull
    @Override
    public Option<Integer> getItemPosition(@NonNull final T item) {
        int position = models.indexOf(item);
        return position < 0 ? Option.NONE : ofObj(position);
    }

    private void assertValidPosition(final int position) {
        checkArgument(position < models.size(), "Position value is too big.");
    }
}
