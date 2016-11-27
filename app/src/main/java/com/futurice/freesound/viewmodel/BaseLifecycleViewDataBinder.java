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

package com.futurice.freesound.viewmodel;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Abstracts the view-to-viewmodel binding mechanism from the views associated lifecycle triggers.
 */
public abstract class BaseLifecycleViewDataBinder implements LifecycleDataBinder {

    @NonNull
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        viewModel().bindToDataModel();
    }

    @Override
    public void onResume() {
        bind(disposable);
    }

    @Override
    public void onPause() {
        disposable.clear();
        unbind();
    }

    @Override
    public void onDestroyView() {
        viewModel().unbindDataModel();
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
    }

    @NonNull
    public abstract ViewModel viewModel();

}
