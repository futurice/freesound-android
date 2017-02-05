package com.futurice.freesound.feature.common.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * Instantiates a {@link ViewHolder} based on the type.
 */
public interface IViewHolderFactory {

    /**
     * Creates a {@link ViewHolder} for the passed type
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @return the newly created {@link ViewHolder}
     */
    @NonNull
    ViewHolder createViewHolder(@NonNull final ViewGroup parent, final int itemType);
}
