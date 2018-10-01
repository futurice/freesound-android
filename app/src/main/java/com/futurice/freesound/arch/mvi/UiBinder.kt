/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.arch.mvi

import android.arch.lifecycle.*

/**
 * The MviView holds this instance.
 */
class UiBinder<E : Event, M : State>(private val mviView: MviView<E, M>,
                                     private val viewModel: ViewModel<E, M>,
                                     val lifecycleOwner: LifecycleOwner) {

    init {
        lifecycleOwner.lifecycle.observeOnCreate { bind() }
    }

    private fun bind() {
        // Send UiEvents to the ViewModel
        mviView.uiEvents()
                .observe(lifecycleOwner, Observer { viewModel.uiEvents(it!!) })

        // Send UiModels to the View
        viewModel.uiModels()
                .observe(lifecycleOwner, Observer { mviView.render(it!!) })

        // Cancels asynchronous actions the view is handling
        lifecycleOwner.lifecycle.observeOnDestroy { mviView.cancel() }
    }

    private fun Lifecycle.observeOnDestroy(action: () -> Unit) {
        this.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed() = action()
        })
    }

    private fun Lifecycle.observeOnCreate(action: () -> Unit) {
        this.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() = action()
        })
    }

}
