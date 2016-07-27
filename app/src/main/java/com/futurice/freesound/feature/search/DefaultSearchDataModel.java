package com.futurice.freesound.feature.search;

import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.FreeSoundSearchService;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.jakewharton.rxrelay.BehaviorRelay;

import android.support.annotation.NonNull;

import java.util.List;

import polanski.option.Option;
import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

final class DefaultSearchDataModel implements SearchDataModel {

    @NonNull
    private final FreeSoundSearchService freeSoundSearchService;

    @NonNull
    private final BehaviorRelay<Option<List<Sound>>> lastResults = BehaviorRelay.create();

    DefaultSearchDataModel(@NonNull final FreeSoundSearchService freeSoundSearchService) {
        this.freeSoundSearchService = get(freeSoundSearchService);
    }

    @Override
    @NonNull
    public Observable<Unit> querySearch(@NonNull final String query) {
        return freeSoundSearchService.search(get(query))
                                     .map(SoundSearchResult::results)
                                     .map(Option::ofObj)
                                     .doOnNext(lastResults)
                                     .map(Unit::asUnit);
    }

    @Override
    @NonNull
    public Observable<Option<List<Sound>>> getSearchResults() {
        return this.lastResults.asObservable();
    }

    @NonNull
    public Observable<Unit> clear() {
        return Observable.just(Option.<List<Sound>>none())
                         .doOnNext(lastResults)
                         .map(__ -> Unit.DEFAULT);
    }
}
