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

import androidx.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;

/**
 * ViewModel that doesn't have a data connection.
 */
public class SimpleViewModel extends BaseViewModel {

    @Override
    protected void bind(@NonNull final CompositeDisposable disposables) {
        // Nothing - has no data source to bind to.
    }

    @Override
    protected void unbind() {
        // Nothing - has no data source to unbind from.
    }
}
