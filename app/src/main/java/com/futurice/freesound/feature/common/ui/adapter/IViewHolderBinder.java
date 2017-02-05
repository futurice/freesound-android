package com.futurice.freesound.feature.common.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Populates a {@link ViewHolder} with the model details.
 */
public interface IViewHolderBinder<T> {

    /**
     * Populates the passed {@link ViewHolder} with the details of the passed model.
     */
    void bind(@NonNull final ViewHolder viewHolder, @NonNull final T model);
}
