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

import android.support.annotation.VisibleForTesting
import com.futurice.freesound.network.api.model.User
import io.reactivex.Observable
import polanski.option.Option


/**
 * Ideally this component could be reused elsewhere.
 *
 * TODO What's the scoping of this?
 * TODO Distinct until changed?
 * TODO FetchCondition- this should be a plugin to interactor
 *  - fetchAlways
 *  - fetchIfNone
 *  - fetchIfError
 *  - fetchTime
 * Repo will just use HTTP cache, so there's no harm in re-requesting each time.
 * Does it even make sense to have a None then?
 *
 */
internal class HomeUserInteractor(private val userRepository: UserRepository) {

    companion object {
        @VisibleForTesting
        val HOME_USERNAME = "SpiceProgram"
    }

    fun getHomeUser(): Observable<FetchResult> {
        return userRepository.user(HOME_USERNAME)
                .switchMap { fetchIfNone(it) }
                .startWith(FetchResult.InProgress)
                .onErrorResumeNext { e: Throwable -> Observable.just(FetchResult.Failure(e)) }
    }

    private fun fetchIfNone(value: Option<User>) = fetchIf(value, { it.isNone })

    private fun <T> fetchIf(value: T, predicate: (T) -> Boolean): Observable<FetchResult> {
        return if (predicate(value)) userRepository.fetchUser(HOME_USERNAME).toObservable()
        else value
    }

    private fun homeUser(): Observable<Option<User>> {
        return userRepository.user(HOME_USERNAME)
    }

    sealed class FetchResult {
        object InProgress : FetchResult()
        class Success<T>(value: T) : FetchResult()
        class Failure(val error: Throwable) : FetchResult()
    }


}