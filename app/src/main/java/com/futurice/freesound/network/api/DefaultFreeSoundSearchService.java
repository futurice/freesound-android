package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundFields;
import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

public final class DefaultFreeSoundSearchService implements FreeSoundSearchService {

    @NonNull
    private final FreeSoundApi freeSoundApi;

    public DefaultFreeSoundSearchService(@NonNull final FreeSoundApi freeSoundApi) {
        this.freeSoundApi = get(freeSoundApi);
    }

    @Override
    @NonNull
    public Observable<SoundSearchResult> search(@NonNull final String query) {
        return freeSoundApi.search(get(query), null, SoundFields.BASE);
    }

}
