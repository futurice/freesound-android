package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.viewmodel.viewholder.BaseBindingViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Populates a {@link ViewHolder} with the model details.
 */
public interface ViewHolderBinder {

    /**
     * Populates the passed {@link ViewHolder} with the details of the passed
     * {@link DisplayableItem}.
     */
    void bind(@NonNull final ViewHolder viewHolder, @NonNull final DisplayableItem item);

    static void unbind(@NonNull final ViewHolder viewHolder) {
        if (viewHolder instanceof BaseBindingViewHolder) {
            ((BaseBindingViewHolder) viewHolder).unbind();
        }
    }
}
