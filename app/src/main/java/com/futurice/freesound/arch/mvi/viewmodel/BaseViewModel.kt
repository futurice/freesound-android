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

package com.futurice.freesound.arch.mvi.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.futurice.freesound.arch.mvi.TransitionEvent
import com.futurice.freesound.arch.mvi.TransitionObserver
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject

/**
 * This class provides the basic mechanism to transform a stream of Events to a stream of States.
 */
abstract class BaseViewModel<E, S>(
        private val initialEvent: E,
        private val schedulerProvider: SchedulerProvider,
        private val transitionObserver: TransitionObserver,
        private val tag: String) : ViewModel(), MviViewModel<E, S> {

    private val events: PublishSubject<E> = PublishSubject.create()
    private val state: MutableLiveData<S> = MutableLiveData()
    private val disposable: SerialDisposable = SerialDisposable()

    protected fun bind() {
        disposable.set(
                events.startWith(initialEvent)
                        .observeOn(schedulerProvider.computation())
                        .asUiEventFlowable()
                        .compose { upstream -> mapEventToStateStream(upstream).asUiModelFlowable() }
                        .subscribe(
                                { state.postValue(it) },
                                { onTransition(TransitionEvent.Error(it)) }))
    }

    override fun uiEvents(uiEvent: E) {
        events.onNext(uiEvent)
    }

    override fun uiModels(): LiveData<S> {
        return state
    }

    override fun onCleared() {
        disposable.dispose()
    }

    fun onTransition(transitionEvent: TransitionEvent) {
        transitionObserver.onTransition(tag, transitionEvent)
    }

    abstract fun mapEventToStateStream(event: Flowable<E>): Flowable<S>

}
