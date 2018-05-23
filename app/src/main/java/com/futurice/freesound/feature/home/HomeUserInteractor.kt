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
import com.futurice.freesound.feature.user.UserRepository
import com.futurice.freesound.network.api.model.User
import io.reactivex.Flowable
import io.reactivex.Single
import polanski.option.Option

/**
 * Ideally this component could be reused elsewhere.
 */
internal class HomeUserInteractor(private val userRepository: UserRepository) {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val HOME_USERNAME = "SpiceProgram"
    }

    fun homeUser(): Flowable<Fetch<User>> {
        return currentHomeUser()
                .toFlowable()
                .flatMap { fetchIfNone(it) }
                .startWith(Fetch.InProgress())
                .onErrorReturn { Fetch.Failure(it) }

        // The problem here is the possible lack of atomic interaction with the value in the
        // store - what if there was Some and then we didn't pass that value through before
        // streaming future changes, then the value could be deleted before and we would never
        // get updates.... although I suppose that would be accurate.
        // The value should always come from the store, though.
    }

    // Get the Optional value from the repo, if it's None, then fetch and then concat with the
    // stream value. Otherwise, return the value and concat with the stream
    private fun fetchIfNone(value: Option<User>): Flowable<out Fetch<User>> =
            value.match({ Flowable.just(Fetch.Success<User>(it)) },
                    {
                        userRepository.fetchUser(HOME_USERNAME)
                                .andThen { userRepository.user(HOME_USERNAME) }
                                .toFlowable()
                    })

    private fun currentHomeUser(): Single<Option<User>> = userRepository.user(HOME_USERNAME)

}
