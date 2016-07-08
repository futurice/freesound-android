package com.futurice.freesound.network.api;

import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

public final class FreeSoundSearchService {

    private static final String SOUND_RESPONSE_OBJECT_FIELDS
            = "id,url,name,tags,description,geotag,username,images";

    @NonNull
    private final FreeSoundApi freeSoundApi;

    public FreeSoundSearchService(@NonNull final FreeSoundApi freeSoundApi) {
        this.freeSoundApi = get(freeSoundApi);
    }

    @NonNull
    public Observable<SoundSearchResult> search(@NonNull final String query) {
        return freeSoundApi.search(get(query), buildGeoSearchFilter(), buildSearchFields());
    }

    @NonNull
    private static String buildSearchFields() {
        return SOUND_RESPONSE_OBJECT_FIELDS;
    }

    @NonNull
    private static String buildGeoSearchFilter() {
        return String.format(ApiConstants.IS_GEO_TAGGED_FILTER_QUERY_PARAM + ":%s", true);
    }

}
