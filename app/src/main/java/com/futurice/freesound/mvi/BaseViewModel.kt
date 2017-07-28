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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

abstract class BaseViewModel<E, D, M, C>(
        dataEvents: Observable<D>,
        schedulers: SchedulerProvider) : ViewModel<E, M>() {

    private val uiEvents: PublishProcessor<E> = PublishProcessor.create()
    private val uiModel: Observable<M>
    private val disposable: SerialDisposable = SerialDisposable()

    init {
        uiModel = reduce(uiEvents.toObservable(), dataEvents).replayingShare()
        disposable.set(uiModel
                .subscribeOn(schedulers.computation())
                .subscribe({ Timber.d("## $it") }, { e -> Timber.e("## $e") }))
    }

    override final fun uiEvents(uiEvent: E) {
        uiEvents.offer(uiEvent)
    }

    override final fun uiModel(): Observable<M> {
        return uiModel
    }

    abstract protected fun reduce(model: M, change: C): M

    abstract fun fromUiEvent(uiEvent: E): C

    abstract fun fromDataEvent(dataEvent: D): C

    abstract protected val INITIAL_UI_STATE: M

    private fun reduce(uiEvents: Observable<E>,
                       dataEvents: Observable<D>): Observable<M> {
        return Observable.merge(processUiEvents(uiEvents), processDataEvents(dataEvents))
                .scan(INITIAL_UI_STATE, { model: M, change -> reduce(model, change) })
                .doOnNext { model: M -> Timber.v(" $model") }
    }

    private fun processUiEvents(uiEvents: Observable<E>): Observable<C>
            = uiEvents.map { fromUiEvent(it) }

    private fun processDataEvents(dataEvents: Observable<D>): Observable<C>
            = dataEvents.map { fromDataEvent(it) }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}
