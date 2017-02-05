package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.feature.common.DisplayableItem;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.List;

final class DiffUtilCallback extends DiffUtil.Callback {

    @NonNull
    private final List<DisplayableItem> mOldItems;

    @NonNull
    private final List<DisplayableItem> mNewItems;

    @NonNull
    private final IListItemComparator mComparator;

    DiffUtilCallback(@NonNull final List<DisplayableItem> oldItems,
                     @NonNull final List<DisplayableItem> newItems,
                     @NonNull final IListItemComparator comparator) {
        mOldItems = oldItems;
        mNewItems = newItems;
        mComparator = comparator;
    }

    @Override
    public int getOldListSize() {
        return mOldItems.size();
    }

    @Override
    public int getNewListSize() {
        return mNewItems.size();
    }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return mComparator.areItemsTheSame(mOldItems.get(oldItemPosition),
                                           mNewItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition,
                                      final int newItemPosition) {
        return mComparator.areContentsTheSame(mOldItems.get(oldItemPosition),
                                              mNewItems.get(newItemPosition));
    }

}
