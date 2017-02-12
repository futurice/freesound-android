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

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class FreeSoundApiInterceptorTest {

    private static final String TEST_CLIENT_SECRET = "test-clientSecret";

    @Rule
    public final MockWebServer mockWebServer = new MockWebServer();

    private OkHttpClient okHttpClient;

    private FreeSoundApiInterceptor interceptor;

    @Before
    public void setUp() throws IOException {
        interceptor = new FreeSoundApiInterceptor(TEST_CLIENT_SECRET);
    }

    @Test
    public void interceptor_addsClientSecretAsApiTokenQueryParameter()
            throws InterruptedException, IOException {
        new ArrangeBuilder()
                .withNewOkHttpClient()
                .withInterceptor(interceptor)
                .withEnqueuedMockResponse();

        okHttpClient.newCall(request()).execute();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath())
                .endsWith(String.format("?%s=%s", ApiConstants.TOKEN_QUERY_PARAM,
                                        TEST_CLIENT_SECRET));
    }

    private Request request() {
        return new Request.Builder().url(mockWebServer.url("/")).build();
    }

    private class ArrangeBuilder {

        ArrangeBuilder withNewOkHttpClient() {
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

}
