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

import android.support.v4.util.LruCache
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.store.Store
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * This implementation makes the assumption that the contents of LruCache can only be
 * changed by its external API.
 *
 * TODO Check the synchronization
 */
internal class UserStore : Store<String, User> {

    // This is the source of truth.
    private val users: LruCache<String, User> = LruCache(100)

    private val usersStream: BehaviorSubject<Set<Map.Entry<String, User>>> = BehaviorSubject.create()

    // Emits the current value, if it exists, else empty
    @Synchronized
    override fun get(key: String): Maybe<User> {
        return users[key]?.let { Maybe.just(it) } ?: Maybe.empty()
    }

    // Emits the current value if it exists, then all future values (does not complete)
    @Synchronized
    override fun getStream(key: String): Observable<User> {
        // If the value is removed from the cache, then this should not complete, it just
        // won't get any more values. That's why using take(1), not firstOrError().
        return usersStream
                .concatMap {
                    Observable.fromIterable(it)
                            .filter { it.key == key }
                            .map { it.value }
                            .take(1)
                }
    }

    // Store the provided value for the given key. Completes when the operation has finished.
    override fun put(key: String, value: User): Completable {
        return Completable.fromAction { storeAndFlush(key, value) }
    }

    @Synchronized
    private fun storeAndFlush(key: String, value: User) {
        Timber.d("Storing $key $value")
        // Put a value into the cache
        // Flush the cache values through the stream
        users.put(key, value)
        usersStream.onNext(users.snapshot().entries)
    }
}
