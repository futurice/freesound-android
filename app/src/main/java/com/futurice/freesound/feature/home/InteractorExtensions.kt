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

package com.futurice.freesound.feature.home

import com.futurice.freesound.feature.common.Fetch
import com.futurice.freesound.feature.common.Operation
import io.reactivex.Observable
import io.reactivex.Single

fun <T> Single<T>.asOperation(): Observable<Operation> {
    return toCompletable()
            .toObservable<Operation>()
            .startWith(Operation.InProgress)
            .concatWith(Observable.just(Operation.Complete))
            .onErrorResumeNext { t: Throwable -> Observable.just(Operation.Failure(t)) }
}

fun <T> Observable<T>.asFetch(): Observable<Fetch<T>> {
    return map { Fetch.Success(it) as Fetch<T> }
            .startWith(Fetch.InProgress())
            .onErrorResumeNext { t: Throwable -> Observable.just(Fetch.Failure(t)) }
}
