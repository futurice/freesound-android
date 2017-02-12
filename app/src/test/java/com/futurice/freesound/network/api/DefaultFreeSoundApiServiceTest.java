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

package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.AccessToken;
import com.futurice.freesound.network.api.model.SoundFields;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.network.api.model.User;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import io.reactivex.Single;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultFreeSoundApiServiceTest {

    private static final String DUMMY_CLIENT_ID = "clientId";

    private static final String DUMMY_CLIENT_SECRET = "clientSecret";

    private static final SoundSearchResult DUMMY_SEARCH_RESULT = TestData.searchResult(5);

    private static final User DUMMY_USER = TestData.user();

    private static final AccessToken DUMMY_ACCESS_TOKEN = TestData.accessToken();

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final Throwable ERROR = new Throwable();

    @Mock
    private FreeSoundApi freeSoundApi;

    private DefaultFreeSoundApiService defaultFreeSoundApiService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        defaultFreeSoundApiService = new DefaultFreeSoundApiService(freeSoundApi,
                                                                    DUMMY_CLIENT_ID,
                                                                    DUMMY_CLIENT_SECRET);
    }

    @Test
    public void getUser_emitsUser_whenApiSuccessful() {
        new Arrangement().withApiUser(DUMMY_USER);

        defaultFreeSoundApiService.getUser("username")
                                  .test()
                                  .assertValue(DUMMY_USER);
    }

    @Test
    public void getUser_invokesApiWithUsernameParameter() {
        new Arrangement().withApiUser(DUMMY_USER);
        String username = "username";

        defaultFreeSoundApiService.getUser(username).subscribe();

        //noinspection deprecation
        verify(freeSoundApi).user(username);
    }

    @Test
    public void getUser_emitsError_whenApiError() {
        new Arrangement().withApiUserError(ERROR);

        defaultFreeSoundApiService.getUser("username")
                                  .test()
                                  .assertError(ERROR);
    }

    @Test
    public void getAccessToken_emitsAccessToken_whenApiSuccessful() {
        new Arrangement().withApiAccessToken(DUMMY_ACCESS_TOKEN);

        defaultFreeSoundApiService.getAccessToken("code")
                                  .test()
                                  .assertValue(DUMMY_ACCESS_TOKEN);
    }

    @Test
    public void getAccessToken_invokesApiWithCorrectParameters() {
        new Arrangement().withApiAccessToken(DUMMY_ACCESS_TOKEN);
        String code = "code";

        defaultFreeSoundApiService.getAccessToken(code).subscribe();

        //noinspection deprecation
        verify(freeSoundApi).accessToken(DUMMY_CLIENT_ID,
                                         DUMMY_CLIENT_SECRET,
                                         ApiConstants.AUTHORIZATION_CODE_GRANT_TYPE_VALUE,
                                         code);
    }

    @Test
    public void getAccessToken_emitsError_whenApiError() {
        new Arrangement().withApiAccessTokenError(ERROR);

        defaultFreeSoundApiService.getAccessToken("code")
                                  .test()
                                  .assertError(ERROR);
    }

    @Test
    public void search_emitsResults_whenApiSuccessful() {
        new Arrangement().withApiSearchResult(DUMMY_SEARCH_RESULT);

        defaultFreeSoundApiService.search("query")
                                  .test()
                                  .assertValue(DUMMY_SEARCH_RESULT);
    }

    @Test
    public void search_invokesApiWithCorrectParameters() {
        new Arrangement().withApiSearchResult(DUMMY_SEARCH_RESULT);
        String query = "query";
        defaultFreeSoundApiService.search(query).subscribe();

        //noinspection deprecation
        verify(freeSoundApi).search(eq(query), isNull(String.class), eq(SoundFields.BASE));
    }

    @Test
    public void search_emitsError_whenApiError() {
        new Arrangement().withApiSearchError(ERROR);

        defaultFreeSoundApiService.search("query")
                                  .test()
                                  .assertError(ERROR);
    }

    private class Arrangement {

        Arrangement withApiSearchResult(@NonNull final SoundSearchResult result) {
            when(freeSoundApi.search(anyString(), any(), any(SoundFields.class)))
                    .thenReturn(Single.just(result));
            return this;
        }

        Arrangement withApiSearchError(@NonNull final Throwable error) {
            when(freeSoundApi.search(anyString(), any(), any(SoundFields.class)))
                    .thenReturn(Single.error(error));
            return this;
        }

        Arrangement withApiUser(@NonNull final User user) {
            when(freeSoundApi.user(anyString())).thenReturn(Single.just(user));
            return this;
        }

        Arrangement withApiUserError(@NonNull final Throwable error) {
            when(freeSoundApi.user(anyString())).thenReturn(Single.error(error));
            return this;
        }

        Arrangement withApiAccessToken(@NonNull final AccessToken accessToken) {
            when(freeSoundApi.accessToken(anyString(),
                                          anyString(),
                                          anyString(),
                                          anyString())).thenReturn(Single.just(accessToken));
            return this;
        }

        Arrangement withApiAccessTokenError(@NonNull final Throwable error) {
            when(freeSoundApi.accessToken(anyString(),
                                          anyString(),
                                          anyString(),
                                          anyString())).thenReturn(Single.error(error));
            return this;
        }

    }

}
