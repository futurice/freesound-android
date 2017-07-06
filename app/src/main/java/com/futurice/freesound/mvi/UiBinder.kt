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

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 *This is Activity lifecycle bound. Do this on start/stop.
 *
 * The View holds this instance.
 */
class UiBinder<M, E>(val renderer: Renderer<M>,
                     val viewModel: ReducingViewModel<E, M>) {

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun bind(uiEvents: Observable<E> = Observable.never()) {
        disposable.add(uiEvents.subscribe { ViewModel.reduce(it) })
        disposable.add(ViewModel.uiModel().subscribe { renderer.render(it) })
    }

    fun unbind() {
        disposable.clear()
    }
}