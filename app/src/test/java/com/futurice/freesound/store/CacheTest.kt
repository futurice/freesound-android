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

import org.junit.Before
import org.junit.Test

class CacheTest {

    private lateinit var cache: Cache<Int, String>

    @Before
    fun setUp() {
        cache = Cache()
    }

    @Test
    fun `put completes`() {
        cache.put(key = 1, value = "ignored").test().assertComplete()
    }

    @Test
    fun `get retrieves put value for key and completes`() {

        arrange {
            stored {
                key = 1
                value = "testUser"
            }
        }

        cache.get(1)
                .test()
                .assertValue("testUser")
                .assertComplete()
    }

    @Test
    fun `get does not emit and then completes when no value`() {

        cache.get(1)
                .test()
                .assertNoValues()
                .assertComplete()
    }

    @Test
    fun `getStream retrieves put value for key and does not complete`() {

        arrange {
            stored {
                key = 1
                value = "testUser"
            }
        }

        cache.getStream(1)
                .test()
                .assertValue("testUser")
                .assertNotComplete()
    }

    @Test
    fun `getStream does not complete when no value`() {
        cache.getStream(1)
                .test()
                .assertNoValues()
                .assertNotComplete()
    }

    private fun arrange(init: Arrangement.() -> Unit) = Arrangement().apply(init)

    private inner class Arrangement {

        fun stored(block: StoredParams.() -> Unit) {
            StoredParams().apply(block)
                    .let { cache.put(it.key, it.value) }
                    .subscribe()
        }

    }

    // TODO Could potentially use delegate to make DSL error messages more meaningful for required props
    private class StoredParams {
        var key: Int = 0
        lateinit var value: String
    }

}