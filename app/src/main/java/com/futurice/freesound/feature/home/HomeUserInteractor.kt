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
import com.futurice.freesound.feature.common.Fetch
import com.futurice.freesound.feature.common.Operation
import com.futurice.freesound.feature.user.UserRepository
import com.futurice.freesound.network.api.model.User
import io.reactivex.Observable

/**
 * Ideally this component could be reused elsewhere.
 */
class HomeUserInteractor(private val userRepository: UserRepository) {

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        val HOME_USERNAME = "SpiceProgram"
    }

    fun refresh(): Observable<Operation> {
        // Ignore the returned value, let homeUserStream emit the change.
        return refreshUser()
                .toCompletable()
                .toObservable<Operation>()
                .startWith(Operation.InProgress)
                .concatWith { Observable.just(Operation.Complete) }
                .onErrorReturn { Operation.Failure(it) }
    }

    /*
     * Emits the any current cached home user, triggers a fetch from the API and then streams
     * further updates.
     */
    fun homeUserStream(): Observable<Fetch<User>> {
        return userStream()
                .map { Fetch.Success(it) as Fetch<User> }
                .startWith(Fetch.InProgress())
                .onErrorReturn { Fetch.Failure(it) }
    }


    private fun refreshUser() = userRepository.refreshUser(HOME_USERNAME)

    private fun userStream() = userRepository.userStream(HOME_USERNAME)

}
