package com.futurice.freesound.network.api;

import android.support.annotation.NonNull;

import retrofit.RequestInterceptor;

import static com.futurice.freesound.utils.Preconditions.get;

public final class FreeSoundApiInterceptor implements RequestInterceptor {

    @NonNull
    private final String apiToken;

    public FreeSoundApiInterceptor(@NonNull final String apiToken) {
        this.apiToken = get(apiToken);
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addQueryParam(ApiConstants.TOKEN_QUERY_PARAM, apiToken);
    }
}
