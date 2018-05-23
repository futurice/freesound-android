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

import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.store.Store
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import java.util.*

// This is a placeholder implementation
internal class UserStore : Store<String, User> {

    private val users: MutableMap<String, User> = TreeMap()

    // Emits the current value, if it exists, then Completes
    override fun get(key: String): Maybe<User> {
        return users[key]?.let { Maybe.just(it) } ?: Maybe.empty()
    }

    // Emits the current value if it exists, then all future values (does not complete)
    override fun getStream(key: String): Observable<User> {
        return users[key]?.let { Observable.never<User>().startWith(it) } ?: Observable.never()
    }

    // Store the provided value for the given key. Completes when the operation has finished.
    override fun put(key: String, value: User): Completable {
        return Completable.fromAction { users[key] = value }
    }
}
