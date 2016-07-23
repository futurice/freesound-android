package com.futurice.freesound.feature.search;

import com.futurice.freesound.functional.Functions;
import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

final class SearchViewModel extends BaseViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final PublishRelay<Unit> clearRelay = PublishRelay.create();

    @NonNull
    private final BehaviorRelay<String> searchTermRelay = BehaviorRelay.create();

    SearchViewModel(@NonNull final SearchDataModel searchDataModel) {
        this.searchDataModel = get(searchDataModel);
    }

    void search(@NonNull final String query) {
        searchTermRelay.call(get(query));
    }

    void clear() {
        clearRelay.call(Unit.DEFAULT);
    }

    @NonNull
    Observable<List<Sound>> getSounds() {
        return searchDataModel.getSearchResults();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        // TODO Use Navigator to open sound details.
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        clearRelay.subscribeOn(AndroidSchedulers.mainThread())
                  .observeOn(Schedulers.computation())
                  .subscribe(__ -> searchDataModel.clear(),
                             e -> Log.e(TAG, "Error clearing search", e));

        searchTermRelay.subscribeOn(AndroidSchedulers.mainThread())
                       .observeOn(Schedulers.computation())
                       //      .debounce(2, TimeUnit.SECONDS)
                       .map(String::trim)
                       .switchMap(searchDataModel::querySearch)
                       .subscribe(Functions.nothing1(),
                                  e -> Log.e(TAG, "Error when setting search term", e));

    }
}
