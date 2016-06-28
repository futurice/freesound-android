package com.futurice.freesound.viewmodel.viewholder;

import com.futurice.freesound.viewmodel.BaseViewModel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract class AbstractBindingViewHolder<T extends BaseViewModel> extends RecyclerView.ViewHolder {

    AbstractBindingViewHolder(final View itemView) {
        super(itemView);
    }

    public abstract void bind(@NonNull final T viewModel);

    public abstract void unbind();
}
