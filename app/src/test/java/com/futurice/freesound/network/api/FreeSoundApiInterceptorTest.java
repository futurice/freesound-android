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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FreeSoundApiInterceptorTest {

    private final static String TEST_TOKEN = "test-token";

    @Rule
    public final MockWebServer mockWebServer = new MockWebServer();

    private OkHttpClient okHttpClient;

    private FreeSoundApiInterceptor interceptor;

    @Before
    public void setUp() throws IOException {
        interceptor = new FreeSoundApiInterceptor(TEST_TOKEN);
    }

    @Test
    public void interceptor_addsApiTokenQueryParameter() throws InterruptedException, IOException {
        new ArrangeBuilder()
                .withNewOkHttpClientInstance()
                .withEnqueuedMockResponse()
                .withInterceptor(interceptor);

        okHttpClient.newCall(request()).execute();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath())
                .endsWith(String.format("?%s=%s", ApiConstants.TOKEN_QUERY_PARAM, TEST_TOKEN));
    }

    @Test
    public void interceptor_proceedsWithApiTokenQueryParameter() throws IOException {
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        Request request = request();
        new ArrangeBuilder2(chain)
                .withChainRequest(request);

        interceptor.intercept(chain);

        ArgumentCaptor<Request> argument = ArgumentCaptor.forClass(Request.class);
        verify(chain).proceed(argument.capture());
        String queryParameter = argument.getValue()
                                        .url()
                                        .queryParameter(ApiConstants.TOKEN_QUERY_PARAM);
        assertThat(queryParameter).isEqualTo(TEST_TOKEN);
    }

    private Request request() {
        return new Request.Builder().url(mockWebServer.url("/")).build();
    }

    private class ArrangeBuilder {

        ArrangeBuilder withNewOkHttpClientInstance() {
            okHttpClient = new OkHttpClient();
            return this;
        }

        ArrangeBuilder withInterceptor(FreeSoundApiInterceptor interceptor) {
            okHttpClient = okHttpClient.newBuilder()
                                       .addInterceptor(interceptor)
                                       .build();
            return this;
        }

        ArrangeBuilder withEnqueuedMockResponse() {
            mockWebServer.enqueue(new MockResponse());
            return this;
        }

    }

    private class ArrangeBuilder2 {

        private Interceptor.Chain chain;

        ArrangeBuilder2(Interceptor.Chain chain) {
            this.chain = chain;
        }

        ArrangeBuilder2 withChainRequest(Request request) throws IOException {
            when(chain.request()).thenReturn(request);
            when(chain.proceed(any(Request.class)))
                    .thenReturn(new Response.Builder()
                                        .protocol(Protocol.HTTP_1_1)
                                        .code(200)
                                        .request(request).build());
            return this;
        }

    }
}
