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
public abstract class BaseLifecycleViewBinder implements LifecycleBinder {

    @NonNull
    private final CompositeDisposable subscription = new CompositeDisposable();

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
        subscription.dispose();
    }

    @NonNull
    public abstract ViewModel viewModel();

}
