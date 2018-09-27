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
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel<in E : Event, A : Action, R : Result, S : State>(
        initialEvent: E,
        eventMapper: EventMapper<E, A>,
        store: Store<A, R, S>,
        schedulerProvider: SchedulerProvider,
        private val uiEvents: PublishSubject<E> = PublishSubject.create(),
        private val uiModel: MutableLiveData<S> = MutableLiveData<S>(),
        private val disposable: SerialDisposable = SerialDisposable(),
        private val logTag: String,
        private val logger: Logger) : ViewModel<E, S>() {

    init {
        disposable.set(
                uiEvents.startWith(initialEvent)
                        .observeOn(schedulerProvider.computation())
                        .doOnNext { logger.log(logTag, LogEvent.Event(it)) }
                        .asUiEventFlowable()
                        .map(eventMapper)
                        .doOnNext { logger.log(logTag, LogEvent.Action(it)) }
                        .compose(store.dispatchAction())
                        .subscribe(
                                { uiModel.postValue(it) },
                                { logger.log(logTag, LogEvent.Error(it)) }))
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

}
