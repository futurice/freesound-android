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

package com.futurice.freesound.network.api

import com.futurice.freesound.network.api.model.AccessToken
import com.futurice.freesound.network.api.model.SoundFields
import com.futurice.freesound.network.api.model.SoundSearchResult
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.test.data.TestData
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class DefaultFreeSoundApiServiceTest {

    private val TEST_CLIENT_ID = "clientId"
    private val TEST_CLIENT_SECRET = "clientSecret"
    private val TEST_SEARCH_RESULT = TestData.searchResult(5)
    private val DUMMY_USER = TestData.user()
    private val DUMMY_ACCESS_TOKEN = TestData.accessToken()
    private val ERROR = Throwable()

    @Mock
    private lateinit var freeSoundApi: FreeSoundApi

    private lateinit var defaultFreeSoundApiService: DefaultFreeSoundApiService

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        defaultFreeSoundApiService = DefaultFreeSoundApiService(freeSoundApi,
                TEST_CLIENT_ID,
                TEST_CLIENT_SECRET)
    }

    @Test
    fun getUser_emitsUser_whenApiSuccessful() {
        arrange {
            user { DUMMY_USER }
        }

        defaultFreeSoundApiService.getUser("username")
                .test()
                .assertValue(DUMMY_USER)
    }

    @Test
    fun getUser_invokesApiWithUsernameParameter() {
        arrange {
            user { DUMMY_USER }
        }
        val username = "username"

        defaultFreeSoundApiService.getUser(username).subscribe()

        verify(freeSoundApi).user(username)
    }

    @Test
    fun getUser_emitsError_whenApiError() {
        arrange {
            userError { ERROR }
        }

        defaultFreeSoundApiService.getUser("username")
                .test()
                .assertError(ERROR)
    }

    @Test
    fun getAccessToken_emitsAccessToken_whenApiSuccessful() {
        arrange {
            token { DUMMY_ACCESS_TOKEN }
        }

        defaultFreeSoundApiService.getAccessToken("code")
                .test()
                .assertValue(DUMMY_ACCESS_TOKEN)
    }

    @Test
    fun getAccessToken_invokesApiWithCorrectParameters() {
        arrange {
            token { DUMMY_ACCESS_TOKEN }
        }
        val code = "code"

        defaultFreeSoundApiService.getAccessToken(code).subscribe()

        verify(freeSoundApi).accessToken(TEST_CLIENT_ID,
                TEST_CLIENT_SECRET,
                ApiConstants.AUTHORIZATION_CODE_GRANT_TYPE_VALUE,
                code)
    }

    @Test
    fun getAccessToken_emitsError_whenApiError() {
        arrange {
            tokenError { ERROR }
        }

        defaultFreeSoundApiService.getAccessToken("code")
                .test()
                .assertError(ERROR)
    }

    @Test
    fun search_emitsResults_whenApiSuccessful() {
        arrange {
            search { TEST_SEARCH_RESULT }
        }

        defaultFreeSoundApiService.search("query")
                .test()
                .assertValue(TEST_SEARCH_RESULT)
    }

    @Test
    fun search_invokesApiWithCorrectParameters() {
        arrange {
            search { TEST_SEARCH_RESULT }
        }
        val query = "query"

        defaultFreeSoundApiService.search(query).subscribe()

        verify(freeSoundApi).search(eq(query), isNull<String>(), eq(SoundFields.BASE))
    }

    @Test
    fun search_emitsError_whenApiError() {
        arrange {
            searchError { ERROR }
        }

        defaultFreeSoundApiService.search("query")
                .test()
                .assertError(ERROR)
    }

    fun arrange(init: Arrangement.() -> Unit) = Arrangement().apply(init)

    inner class Arrangement {

        fun search(init: () -> SoundSearchResult): Any =
                `when`(freeSoundApi.search(anyString(), any<String>(), any<SoundFields>()))
                        .thenReturn(Single.just(init()))

        fun searchError(init: () -> Throwable): Any =
                `when`(freeSoundApi.search(anyString(), any<String>(), any<SoundFields>()))
                        .thenReturn(Single.error<SoundSearchResult>(init()))

        fun user(init: () -> User): Any =
                `when`(freeSoundApi.user(anyString())).thenReturn(Single.just(init()))

        fun userError(init: () -> Throwable): Any =
                `when`(freeSoundApi.user(anyString())).thenReturn(Single.error<User>(init()))

        fun token(init: () -> AccessToken): Any =
                `when`(freeSoundApi.accessToken(anyString(),
                        anyString(),
                        anyString(),
                        anyString())).thenReturn(Single.just(init()))

        fun tokenError(init: () -> Throwable): Any =
                `when`(freeSoundApi.accessToken(anyString(),
                        anyString(),
                        anyString(),
                        anyString())).thenReturn(Single.error<AccessToken>(init()))
    }

}
