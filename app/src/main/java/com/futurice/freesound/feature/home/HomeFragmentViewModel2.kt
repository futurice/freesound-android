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

package com.futurice.freesound.feature.home

import com.futurice.freesound.mvi.Reducer
import com.futurice.freesound.mvi.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

internal class HomeFragmentViewModel2(private val dataEvents: Observable<Fragment.DataEvent>)
    : Reducer<Fragment.UiEvent, Fragment.UiModel>, ViewModel<Fragment.UiEvent, Fragment.UiModel>() {

    private val uiEvents: PublishProcessor<Fragment.UiEvent> = PublishProcessor.create()
    private val uiModel: PublishProcessor<Fragment.UiModel> = PublishProcessor.create()

    private val disposable: Disposable

    init {
        disposable = reduce(uiEvents.toObservable())
                .subscribe { uiModel.offer(it) }
    }

    override fun uiEvents(uiEvent: Fragment.UiEvent) {
        uiEvents.offer(uiEvent)
    }

    override fun uiModel(): Observable<Fragment.UiModel> {
        return uiModel.toObservable()
    }

    override fun reduce(input: Observable<Fragment.UiEvent>): Observable<Fragment.UiModel> {
        return Observable.merge(processUiEvents(input), processDataEvents(dataEvents))
                .scan(INITIAL_UI_STATE, { model, change -> model.reduce(change) })
                .doOnNext { model: Fragment.UiModel -> Timber.v(" $model") }
    }

    private fun processUiEvents(uiEvents: Observable<Fragment.UiEvent>): Observable<Fragment.Change>
            = uiEvents.map { toChange(it) }

    private fun processDataEvents(dataEvents: Observable<Fragment.DataEvent>): Observable<Fragment.Change>
            = dataEvents.map { toChange(it) }

    private fun toChange(uiEvent: Fragment.UiEvent): Fragment.Change =
            when (uiEvent) {
                is Fragment.UiEvent.NoOp -> Fragment.Change.NoOp
            }

    private fun toChange(dataEvent: Fragment.DataEvent): Fragment.Change =
            when (dataEvent) {
                is Fragment.DataEvent.UserDataEvent -> Fragment.Change.UserModified(dataEvent.user)
            }

    private fun Fragment.UiModel.reduce(event: Fragment.Change): Fragment.UiModel =
            when (event) {
                Fragment.Change.NoOp -> this
                is Fragment.Change.UserModified ->
                    copy(username = event.user.username(),
                            about = event.user.about(),
                            avatarUrl = event.user.avatar().large())
            }

    companion object {
        // TODO The real initial state comes from whatever is in the data layer
        // It seems odd to have to define something here when that default is essentially None
        val INITIAL_UI_STATE: Fragment.UiModel by lazy { Fragment.UiModel("", "", "") }
    }

}