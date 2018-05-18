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

package com.futurice.freesound.mvi

import android.arch.lifecycle.*
import org.reactivestreams.Publisher
import timber.log.Timber

/**
 * The MviView holds this instance.
 */
class UiBinder<M, E>(private val mviView: MviView<E, M>,
                     private val viewModel: ViewModel<E, M>,
                     lifecycleOwner: LifecycleOwner) {

    // LiveData thinks the events can be null, but they can't because they come from RxJava 2 sources.

    // Note: The Lifecycle observer doesn't handle errors in the stream - it will crash.
    // The ViewModel should log as side effect and propagate an UnexpectedError event as a part of the stream.

    init {
        mviView.uiEvents()
                .observe(lifecycleOwner, Observer { viewModel.uiEvents(it!!) })

        viewModel.uiModels()
                .doOnError {
                    Timber.e(it, "Fatal unhandled error dispatching UI model. " +
                            " You should have caught this in the ViewModel.")
                }
                .observe(lifecycleOwner, Observer { mviView.render(it!!) })

        // Cancels any asynchronous actions that the view is handling (e.g. image loading)
        lifecycleOwner.lifecycle.observeDestroy { mviView.cancel() }
    }

    private fun <T> Publisher<T>.observe(owner: LifecycleOwner, observer: Observer<T>) {
        LiveDataReactiveStreams.fromPublisher(this).observe(owner, observer)
    }

    private fun Lifecycle.observeDestroy(action: () -> Unit) {
        this.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed() = action()
        })
    }

}
