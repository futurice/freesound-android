package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface FreeSoundApi {

    @NonNull
    @GET("/apiv2/search/text/")
    Observable<SoundSearchResult> search(@Query("query") @NonNull final String query,
                                         @Query("filter") @Nullable final String filter,
                                         @Query("fields") @Nullable final String fields);
}
