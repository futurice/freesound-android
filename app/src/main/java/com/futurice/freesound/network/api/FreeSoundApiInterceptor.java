package com.futurice.freesound.network.api;

import android.support.annotation.NonNull;

import retrofit.RequestInterceptor;

public final class FreeSoundApiInterceptor implements RequestInterceptor {

    @NonNull
    private final String apiToken;

    public FreeSoundApiInterceptor(@NonNull final String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addQueryParam(ApiConstants.TOKEN_QUERY_PARAM, apiToken);
    }
}
