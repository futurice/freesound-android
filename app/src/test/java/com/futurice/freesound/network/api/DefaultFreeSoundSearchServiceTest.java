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

import com.futurice.freesound.network.api.model.SoundFields;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.test.data.TestData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.futurice.freesound.test.utils.TestSubscriberUtils.testSubscribe;
import static com.petertackage.assertrx.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultFreeSoundSearchServiceTest {

    private static final SoundSearchResult RESULT = TestData.searchResult(5);

    private static final String QUERY = "query";

    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final Throwable ERROR = new Throwable();

    @Mock
    private FreeSoundApi freeSoundApi;

    private DefaultFreeSoundSearchService defaultFreeSoundSearchService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        defaultFreeSoundSearchService = new DefaultFreeSoundSearchService(freeSoundApi);
    }

    @Test
    public void search_emitsResults_whenApiSuccessful() {
        new Arrangement().withApiResultResult(RESULT);

        TestSubscriber<SoundSearchResult> ts =
                testSubscribe(defaultFreeSoundSearchService.search(QUERY));

        assertThat(ts).hasNoErrors()
                      .hasReceivedValue(RESULT);
    }

    @Test
    public void search_invokesApiWithCorrectParameters() {
        new Arrangement().withApiResultResult(RESULT);

        testSubscribe(defaultFreeSoundSearchService.search(QUERY));

        //noinspection deprecation
        verify(freeSoundApi).search(eq(QUERY), isNull(String.class), eq(SoundFields.BASE));
    }

    @Test
    public void search_emitsError_whenApiError() {
        new Arrangement().withApiError(ERROR);

        TestSubscriber<SoundSearchResult> ts =
                testSubscribe(defaultFreeSoundSearchService.search(QUERY));

        assertThat(ts).hasNoValues()
                      .hasError(ERROR);
    }

    private class Arrangement {

        Arrangement withApiResultResult(@NonNull final SoundSearchResult result) {
            when(freeSoundApi.search(anyString(), any(), any(SoundFields.class)))
                    .thenReturn(Observable.just(result));
            return this;
        }

        Arrangement withApiError(@NonNull final Throwable error) {
            when(freeSoundApi.search(anyString(), any(), any(SoundFields.class)))
                    .thenReturn(Observable.error(error));
            return this;
        }

    }

}
