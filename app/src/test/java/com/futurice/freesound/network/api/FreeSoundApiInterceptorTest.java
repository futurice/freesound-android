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

import org.junit.Test;

//import retrofit.RequestInterceptor.RequestFacade;
//
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//public class FreeSoundApiInterceptorTest {
//
//    private static final String DUMMY_TOKEN = "token";
//
//    private FreeSoundApiInterceptor freeSoundApiInterceptor;
//
//    public FreeSoundApiInterceptorTest() {
//        this.freeSoundApiInterceptor = new FreeSoundApiInterceptor(DUMMY_TOKEN);
//    }
//
//    @Test
//    public void intercept_addsTokenQueryParameter() {
//        RequestFacade requestFacade = mock(RequestFacade.class);
//
//        freeSoundApiInterceptor.intercept(requestFacade);
//
//        verify(requestFacade).addQueryParam(eq(ApiConstants.TOKEN_QUERY_PARAM), eq(DUMMY_TOKEN));
//    }
//
//}
