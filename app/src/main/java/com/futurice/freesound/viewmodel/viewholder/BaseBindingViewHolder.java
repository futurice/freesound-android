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

package com.futurice.freesound.viewmodel.viewholder;

import com.futurice.freesound.viewmodel.BaseViewModel;
import com.futurice.freesound.viewmodel.DataBinder;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import io.reactivex.disposables.CompositeDisposable;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;
import static com.futurice.freesound.utils.Preconditions.get;

public abstract class BaseBindingViewHolder<T extends BaseViewModel>
        extends AbstractBindingViewHolder<T> {

    @Nullable
    private T viewModel;

    @NonNull
    private final CompositeDisposable disposables = new CompositeDisposable();

    protected BaseBindingViewHolder(@NonNull final View view) {
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
    protected abstract DataBinder getViewDataBinder();

    @Nullable
    protected final T getViewModel() {
        return viewModel;
    }

    private void bindViewToViewModel() {
        getViewDataBinder().bind(disposables);
    }

    private void setAndBindDataModel(@NonNull final T viewModel) {
        this.viewModel = viewModel;
        viewModel.bindToDataModel();
    }

    private void unbindViewFromViewModel() {
        // Don't unsubscribe - we need to reuse it!
        disposables.clear();
        getViewDataBinder().unbind();
    }

    private void unbindViewModelFromData() {
        checkNotNull(viewModel, "View Model cannot be null when unbinding");
        viewModel.unbindDataModel();
        viewModel = null;
    }

}
