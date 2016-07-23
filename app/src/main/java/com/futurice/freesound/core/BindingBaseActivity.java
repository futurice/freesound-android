package com.futurice.freesound.core;

import com.futurice.freesound.viewmodel.BaseLifecycleViewBinder;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.ViewModel;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.subscriptions.CompositeSubscription;

public abstract class BindingBaseActivity<T> extends BaseActivity<T> {

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
            return BindingBaseActivity.this.viewModel();
        }

    };

    @CallSuper
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleBinder.onCreate();
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        lifecycleBinder.onResume();
    }

    @CallSuper
    @Override
    public void onPause() {
        lifecycleBinder.onPause();
        super.onPause();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        lifecycleBinder.onDestroyView();
        lifecycleBinder.onDestroy();
        super.onDestroy();
    }

    @NonNull
    protected abstract ViewModel viewModel();

    @NonNull
    protected abstract Binder binder();
}
