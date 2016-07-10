package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import rx.Observable;

public interface FreeSoundSearchService {

    @NonNull
    Observable<SoundSearchResult> search(@NonNull String query);
}
