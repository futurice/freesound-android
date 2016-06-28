package com.futurice.freesound.feature.search;

import com.futurice.freesound.Unit;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.jakewharton.rxrelay.BehaviorRelay;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

public class SearchDataModel {

    @NonNull
    private final FreeSoundApi freeSoundApi;

    @NonNull
    private final BehaviorRelay<List<Sound>> lastResults = BehaviorRelay.create();

    SearchDataModel(@NonNull final FreeSoundApi freeSoundApi) {
        this.freeSoundApi = get(freeSoundApi);
    }

    @NonNull
    Observable<Unit> querySearch(@NonNull final String query) {
        return freeSoundApi.search(get(query), null, null)
                           .map(SoundSearchResult::results)
                           .doOnNext(lastResults)
                           .map(Unit::asUnit);
    }

    @NonNull
    Observable<List<Sound>> getSearchResults() {
        return this.lastResults.asObservable();
    }

    @NonNull
    Observable<Unit> clear() {
        // TODO Implement clear
        return Observable.just(Unit.DEFAULT);
    }
}
