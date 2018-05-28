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

package com.futurice.freesound.feature.user

import com.futurice.freesound.network.api.FreeSoundApiService
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.store.CacheStore
import io.reactivex.Observable
import io.reactivex.Single

/*
 * refresh: Active, Single: Always fetches, stores and emits once.
 * get: Active, Single: Returns cache if it exists, otherwise it fetches, stores and emits once.
 * getStream: Active, Observable: Returns cache if it exists, fetches, stores and emits value and future values.
 * await: Passive, Single TODO
 * awaitStream Passive, Observable TODO
 *
 * Question: Should we just return the fetched value or always use the value in the store?
 * By emitting the fetched value, we are assuming that the store does not alter that.
 */
class UserRepository(private val freeSoundApi: FreeSoundApiService,
                     private val userStore: CacheStore<String, User>) {

    // refresh
    fun refreshUser(username: String): Single<User> {
        return freeSoundApi.getUser(username)
                .flatMap { user -> userStore.put(username, user).toSingle { user } } // emits fetched/stored.
    }

    // get
    fun user(username: String): Single<User> {
        return userStore.get(username)
                .switchIfEmpty(refreshUser(username)) // emits fetched/stored
    }

    // getStream
    fun userStream(username: String): Observable<User> {
        return user(username) // emits fetched/stored
                .toObservable()
                .concatWith { userStore.getStream(username).skip(1) }
    }

}
