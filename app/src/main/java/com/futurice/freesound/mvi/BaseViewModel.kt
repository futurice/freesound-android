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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel<in E : Event, A : Action, R : Result, S : State>(
        private val tag: String,
        private val logger: Logger,
        initialEvent: E,
        store: Store<A, R, S>,
        private val disposable: SerialDisposable = SerialDisposable(),
        private val uiEvents: PublishSubject<E> = PublishSubject.create(),
        private val uiModel: MutableLiveData<S> = MutableLiveData<S>()) : ViewModel<E, S>() {

    init {
        disposable.set(
                uiEvents.startWith(initialEvent)
                        .doOnNext { logger.log(tag, LogEvent.Event(it)) }
                        .asUiEventFlowable()
                        .map(::eventToAction)
                        .doOnNext { logger.log(tag, LogEvent.Action(it)) }
                        .compose(store.dispatchAction())
                        .subscribe(
                                { uiModel.postValue(it) },
                                { logger.log(tag, LogEvent.Error(it)) }))
    }

    override fun uiEvents(uiEvent: E) {
        uiEvents.onNext(uiEvent)
    }

    override fun uiModels(): LiveData<S> {
        return uiModel
    }

    override fun onCleared() {
        disposable.dispose()
    }

    abstract fun eventToAction(uiEvent: E): A

}
