package com.futurice.freesound.core;

import com.futurice.freesound.viewmodel.BaseLifecycleViewBinder;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.subscriptions.CompositeSubscription;

/**
 * The base Fragment to be used when the Fragment View is to be bound to a ViewModel.
 */
public abstract class BaseBindingFragment<T> extends BaseFragment<T> {

    // TODO This binder is a good candidate for a trait in Kotlin

    @NonNull
    private final BaseLifecycleViewBinder lifecycleBinder = new BaseLifecycleViewBinder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            binder().bind(subscription);
        }

        @Override
        public void unbind() {
            binder().unbind();
        }

        @NonNull
        @Override
        public ViewModel viewModel() {
            return BaseBindingFragment.this.viewModel();
        }

    };

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lifecycleBinder.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleBinder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        lifecycleBinder.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lifecycleBinder.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        lifecycleBinder.onDestroy();
    }

    @NonNull
    protected abstract ViewModel viewModel();

    @NonNull
    protected abstract Binder binder();
}
