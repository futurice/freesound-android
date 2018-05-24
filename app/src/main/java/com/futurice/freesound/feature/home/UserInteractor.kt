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
import com.futurice.freesound.feature.common.Operation
import com.futurice.freesound.feature.user.UserRepository
import com.futurice.freesound.network.api.model.User
import io.reactivex.Observable
import io.reactivex.Single
import polanski.option.Option
import polanski.option.OptionUnsafe

/**
 * Ideally this component could be reused elsewhere.
 */
class UserInteractor(private val userRepository: UserRepository) {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val HOME_USERNAME = "SpiceProgram"
    }

    fun fetchHomeUser(): Observable<Operation> {
        return currentHomeUser()
                .toObservable()
                .flatMap { fetchIfNone(it) }
                .startWith(Operation.InProgress)
                .onErrorReturn { Operation.Failure(it) }
    }

    fun homeUserStream(): Observable<User> {
        return userRepository.userStream(HOME_USERNAME)
                .filter { it.isSome }
                .map { OptionUnsafe.getUnsafe(it) }
    }

    private fun fetchIfNone(value: Option<User>): Observable<out Operation> =
            value.match({ Observable.just(Operation.Complete) },
                    {
                        userRepository.fetchUser(HOME_USERNAME)
                                .andThen(Observable.just(Operation.Complete))
                    })

    private fun currentHomeUser(): Single<Option<User>> = userRepository.user(HOME_USERNAME)

}
