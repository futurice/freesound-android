package com.futurice.freesound.core;

import com.futurice.freesound.inject.Injector;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * A base Fragment which provides a dependency injection mechanism.
 *
 * @param <T> The DI component class
 */
public abstract class BaseFragment<T> extends Fragment implements Injector<T> {

    @Nullable
    protected T component;

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject();
    }

    @NonNull
    @Override
    public T component() {
        if (component == null) {
            component = createComponent();
        }
        return component;
    }

    @NonNull
    protected abstract T createComponent();

}
