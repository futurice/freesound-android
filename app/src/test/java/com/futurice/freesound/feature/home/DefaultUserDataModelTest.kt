/*
 * Copyright 2016 Futurice GmbH
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
import com.futurice.freesound.test.data.TestData
import com.futurice.freesound.test.mock
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class DefaultUserDataModelTest {

    @Mock
    private lateinit var freeSoundApiService: FreeSoundApiService

    private lateinit var dataModel: DefaultUserDataModel

    private lateinit var USER: User

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        dataModel = DefaultUserDataModel(freeSoundApiService)
        USER = TestData.user()
    }

    @Test
    fun getHomeUser_looksForSpiceProgram() {
        arrange {
            user { USER }
        }
        dataModel.homeUser

        verify(freeSoundApiService).getUser(DefaultUserDataModel.USER_NAME)
    }

    @Test
    fun getHomeUser_returnsResultOfSearch() {
        val result = USER
        arrange {
            user { result }
        }

        dataModel.homeUser
                .test()
                .assertValue(result)
    }

    fun arrange(init: ArrangeBuilder.() -> Unit) = ArrangeBuilder().apply(init)

    inner class ArrangeBuilder {
        fun user(user: () -> User): Any =
                `when`(freeSoundApiService.getUser(anyString())).thenReturn(Single.just(user()))
    }
}
