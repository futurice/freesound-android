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

import com.futurice.freesound.network.api.FreeSoundApiService
import com.futurice.freesound.network.api.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import polanski.option.Option

class UserRepository(private val freeSoundApi: FreeSoundApiService,
                     private val userStore: Store<String, User>) {

    fun fetchUser(username: String): Completable {
        return freeSoundApi.getUser(username)
                .doOnSuccess { userStore.put(username, it) }
                .toCompletable()
    }

    fun user(username: String): Observable<Option<User>> {
        return userStore.get(username)
    }
}
