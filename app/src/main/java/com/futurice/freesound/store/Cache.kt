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

package com.futurice.freesound.store

import androidx.collection.LruCache
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * A reactive cache.
 */
internal class Cache<K, V>(initialSize: Int = 100) : Store<K, V> {

    // This is the source of truth.
    private val cache: LruCache<K, V> = LruCache(initialSize)

    private val cacheStream: BehaviorSubject<Set<Map.Entry<K, V>>> = BehaviorSubject.create()

    // Emits the current value, if it exists, else empty.
    override fun get(key: K): Maybe<V> {
        return cache[key]?.let { Maybe.just(it) } ?: Maybe.empty()
    }

    // Emits the current value if it exists, then future values while the value is still in the
    // cache. Does not complete.
    override fun getStream(key: K): Observable<V> {
        // If the value is removed from the cache, then this will not complete, it just
        // won't get any more values. That's why using take(1), not firstOrError().
        return cacheStream
                .concatMap {
                    Observable.fromIterable(it)
                            .filter { it.key == key }
                            .map { it.value }
                            .take(1)
                }
    }

    // Store the provided value for the given key. Completes when the operation has finished.
    override fun put(key: K, value: V): Completable {
        return Completable.fromAction { storeAndFlush(key, value) }
    }

    private fun storeAndFlush(key: K, value: V) {
        // Put a value into the cache and flush the cache values through the stream.
        synchronized(cache) {
            cache.put(key, value)
            cacheStream.onNext(cache.snapshot().entries)
        }
    }
}
