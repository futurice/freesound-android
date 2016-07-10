package com.futurice.freesound.feature.search;

import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.model.Sound;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

public interface SearchDataModel {

    @NonNull
    Observable<Unit> querySearch(@NonNull String query);

    @NonNull
    Observable<List<Sound>> getSearchResults();

    @NonNull
    Observable<Unit> clear();
}
