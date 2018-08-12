/*
 * Copyright 2018 Futurice GmbH
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

package com.futurice.freesound.mvi;

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

// For consumable values, we just take the latest if backpressure.
fun <T> Observable<T>.asUiModelFlowable(): Flowable<T> {
    return toFlowable(BackpressureStrategy.LATEST)
}

// For consumable values, we just take the latest if backpressure.
fun <T> Flowable<T>.asUiModelFlowable(): Flowable<T> {
    return onBackpressureLatest()
}


// Events from the UI are modelled as a buffering Observable.
fun <T> Observable<T>.asUiEventFlowable(): Flowable<T> {
    return toFlowable(BackpressureStrategy.BUFFER)
}