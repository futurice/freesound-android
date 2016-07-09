package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * @author Peter Tackage
 * @since 08/07/16
 */
public interface FreeSoundSearchService {

    @NonNull
    Observable<SoundSearchResult> search(@NonNull String query);
}
