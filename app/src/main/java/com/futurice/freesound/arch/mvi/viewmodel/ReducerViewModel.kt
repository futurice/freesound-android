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

import com.futurice.freesound.arch.mvi.Dispatcher
import com.futurice.freesound.arch.mvi.TransitionEvent
import com.futurice.freesound.arch.mvi.TransitionObserver
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import io.reactivex.Flowable

/**
 * Provides the hooks to transform events->actions->results->states.
 *
 * This is a more involved version of the transformation process.
 */
abstract class ReducerViewModel<E, A, R, S>(initialEvent: E, schedulerProvider: SchedulerProvider,
                                            transitionObserver: TransitionObserver, tag: String)
    : BaseViewModel<E, S>(initialEvent, schedulerProvider, transitionObserver, tag) {

    override fun mapEventToStateStream(event: Flowable<E>): Flowable<S> {
        return event
                .startWith(initialEvent())
                .doOnNext { onTransition(TransitionEvent.Event(it as Any)) }
                .map(::this@ReducerViewModel.map)
                .doOnNext { onTransition(TransitionEvent.Action(it as Any)) }
                .compose(dispatch())
                .doOnNext { onTransition(TransitionEvent.Result(it as Any)) }
                .compose { it.scan(initialUiState(), reduce()) }
                .doOnNext { onTransition(TransitionEvent.State(it as Any)) }
    }

    abstract fun initialEvent(): E

    abstract fun initialUiState(): S

    protected abstract fun map(event: E): A

    protected abstract fun dispatch(): Dispatcher<A, R>

    protected abstract fun reduce(): (S, R) -> S
}
