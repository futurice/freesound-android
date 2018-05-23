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
import com.futurice.freesound.test.data.TestData
import org.junit.Before
import org.junit.Test

class UserStoreTest {

    private lateinit var userStore: UserStore

    private val testUser: User get() = TestData.user()

    @Before
    fun setUp() {
        userStore = UserStore()
    }

    @Test
    fun `store completes on put`() {
        userStore.put(key = "username", value = testUser).test().assertComplete()
    }

    @Test
    fun `store get retrieves put value for key and completes`() {

        arrange {
            stored {
                key = "abc"
                value = testUser
            }
        }

        userStore.get("abc")
                .test()
                .assertValue(testUser)
                .assertComplete()
    }

    @Test
    fun `store does not emit and then completes when no value`() {

        userStore.get("abc")
                .test()
                .assertNoValues()
                .assertComplete()
    }

    @Test
    fun `store getStream retrieves put value for key and does not complete`() {

        arrange {
            stored {
                key = "abc"
                value = testUser
            }
        }

        userStore.getStream("abc")
                .test()
                .assertValue(testUser)
                .assertNotComplete()
    }

    @Test
    fun `store getStream does not complete when no value`() {
        userStore.getStream("abc")
                .test()
                .assertNoValues()
                .assertNotComplete()
    }

    private fun arrange(init: Arrangement.() -> Unit) = Arrangement().apply(init)

    private inner class Arrangement {

        fun stored(block: StoredParams.() -> Unit) {
            StoredParams().apply(block)
                    .let { userStore.put(it.key, it.value) }
                    .subscribe()
        }

    }

    // TODO Could potentially use delegate to make DSL error messages more meaningful for required props
    private class StoredParams {
        lateinit var key: String
        lateinit var value: User
    }

}