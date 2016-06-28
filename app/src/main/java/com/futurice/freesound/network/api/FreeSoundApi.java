package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface FreeSoundApi {

    @NonNull
    @GET("/apiv2/search/text/")
    Observable<SoundSearchResult> search(@Query("query") @NonNull final String query,
                                         @Query("filter") @NonNull final String filter,
                                         @Query("fields") @NonNull final String fields);
}
