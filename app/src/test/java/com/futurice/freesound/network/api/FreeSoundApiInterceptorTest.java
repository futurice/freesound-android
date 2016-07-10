package com.futurice.freesound.network.api;

import org.junit.Test;

import retrofit.RequestInterceptor.RequestFacade;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FreeSoundApiInterceptorTest {

    private static final String DUMMY_TOKEN = "token";

    private FreeSoundApiInterceptor freeSoundApiInterceptor;

    public FreeSoundApiInterceptorTest() {
        this.freeSoundApiInterceptor = new FreeSoundApiInterceptor(DUMMY_TOKEN);
    }

    @Test
    public void intercept_addsTokenQueryParameter() {
        RequestFacade requestFacade = mock(RequestFacade.class);

        freeSoundApiInterceptor.intercept(requestFacade);

        verify(requestFacade).addQueryParam(eq(ApiConstants.TOKEN_QUERY_PARAM), eq(DUMMY_TOKEN));
    }

}
