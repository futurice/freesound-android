/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.arch.mvvm;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Abstracts the view-to-viewmodel binding mechanism from the views associated lifecycle triggers.
 *
 * The View should implement a concrete instance to allow it bind to the time-variant (Observable)
 * values provided by its associated {@link ViewModel}.
 */
public abstract class BaseLifecycleViewDataBinder implements LifecycleDataBinder {

    @NonNull
    private final CompositeDisposable disposable = new CompositeDisposable();

    /**
     *  Inform the ViewModel that it needs to bind to the View's modelled data sources.
     */
    @Override
    public void onCreate() {
        viewModel().bindToDataModel();
    }

    /**
     * Bind the View to the ViewModel's data sources.
     */
    @Override
    public void onResume() {
        bind(disposable);
    }

    /**
     * Unbind the View from the ViewModel's data sources.
     */
    @Override
    public void onPause() {
        disposable.clear();
        unbind();
    }

    /**
     * Inform the ViewModel that it needs to unbind from the View's modelled data sources.
     */
    @Override
    public void onDestroyView() {
        viewModel().unbindDataModel();
    }

    /**
     * Permanently dispose of any resources held.
     *
     * The instance cannot be reused after this operation.
     */
    @Override
    public void onDestroy() {
        disposable.dispose();
    }

    /**
     * The {@link ViewModel} to which this binder should bind/unbind.
     *
     * @return the {@link ViewModel} instance.
     */
    @NonNull
    public abstract ViewModel viewModel();

}
