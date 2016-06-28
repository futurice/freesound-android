package com.futurice.freesound.viewmodel;

import android.support.annotation.NonNull;

import rx.subscriptions.CompositeSubscription;

/**
 * Abstracts the view-to-viewmodel binding mechanism from the views associated lifecycle triggers.
 */
public abstract class BaseLifecycleViewBinder implements LifecycleBinder {

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public void onCreate() {
        viewModel().bindToDataModel();
    }

    @Override
    public void onResume() {
        bind(subscription);
    }

    @Override
    public void onPause() {
        subscription.clear();
        unbind();
    }

    @Override
    public void onDestroyView() {
        viewModel().unbindDataModel();
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
    }

    @NonNull
    public abstract ViewModel viewModel();

}
