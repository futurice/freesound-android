package com.futurice.freesound.viewmodel.viewholder;

import com.futurice.freesound.viewmodel.BaseViewModel;
import com.futurice.freesound.viewmodel.Binder;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;
import static com.futurice.freesound.utils.Preconditions.get;

public abstract class BaseBindingViewHolder<T extends BaseViewModel>
        extends AbstractBindingViewHolder<T> {

    @Nullable
    private T viewModel;

    @NonNull
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public BaseBindingViewHolder(@NonNull final View view) {
        super(get(view));
    }

    @CallSuper
    @Override
    public final void bind(@NonNull final T viewModel) {
        setAndBindDataModel(get(viewModel));
        bindViewToViewModel();
    }

    @CallSuper
    @Override
    public final void unbind() {
        unbindViewFromViewModel();
        unbindViewModelFromData();
    }

    @NonNull
    protected abstract Binder getViewBinder();

    @Nullable
    protected final T getViewModel() {
        return viewModel;
    }

    private void bindViewToViewModel() {
        getViewBinder().bind(subscriptions);
    }

    private void setAndBindDataModel(final @NonNull T viewModel) {
        this.viewModel = viewModel;
        viewModel.bindToDataModel();
    }

    private void unbindViewFromViewModel() {
        // Don't unsubscribe - we need to reuse it!
        subscriptions.clear();
        getViewBinder().unbind();
    }

    private void unbindViewModelFromData() {
        checkNotNull(viewModel, "View Model cannot be null when unbinding");
        viewModel.unbindDataModel();
        viewModel = null;
    }

}
