package com.futurice.freesound.feature.common.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * Instantiates a {@link ViewHolder} based on the type.
 */
public abstract class ViewHolderFactory {

    @NonNull
    protected final Context context;

    protected ViewHolderFactory(@NonNull final Context context) {
        this.context = context;
    }

    /**
     * Creates a {@link ViewHolder}
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @return the newly created {@link ViewHolder}
     */
    @NonNull
    public abstract ViewHolder createViewHolder(@NonNull final ViewGroup parent);
}
