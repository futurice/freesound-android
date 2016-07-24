package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.functional.Functions;
import com.futurice.freesound.functional.StringFunctions;
import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.utils.TextUtils;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private final Navigator navigator;

    @NonNull
    private final PublishRelay<Unit> clearRelay = PublishRelay.create();

    @NonNull
    private final BehaviorRelay<String> searchTermRelay = BehaviorRelay.create("");

    @NonNull
    private final PublishRelay<SearchQuery> searchQueryRelay = PublishRelay.create();

    SearchViewModel(@NonNull final SearchDataModel searchDataModel,
                    @NonNull final Navigator navigator) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
    }

    @NonNull
    Observable<SearchQuery> getQuery() {
        return searchQueryRelay.asObservable();
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
        navigator.openSoundDetails(get(sound));
    }

    boolean onCloseSearch(@Nullable final CharSequence currentQuery) {
        if (TextUtils.isNullOrEmpty(currentQuery)) {
            closeSearch();
        } else {
            clear();
        }
        return true;
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        clearRelay.subscribeOn(AndroidSchedulers.mainThread())
                  .doOnNext(__ -> searchTermRelay.call(""))
                  .observeOn(Schedulers.computation())
                  .subscribe(__ -> searchDataModel.clear(),
                             e -> Log.e(TAG, "Error clearing search", e));

        searchTermRelay.subscribeOn(AndroidSchedulers.mainThread())
                       .observeOn(Schedulers.computation())
                       .map(String::trim)
                       .doOnNext(this::publishQueryState)
                       .filter(StringFunctions.isNotEmpty())
                       .debounce(2, TimeUnit.SECONDS)
                       .switchMap(searchDataModel::querySearch)
                       .subscribe(Functions.nothing1(),
                                  e -> Log.e(TAG, "Error when setting search term", e));

    }

    private void publishQueryState(final String q) {
        searchQueryRelay.call(SearchQuery.create(q, TextUtils.isNotNullOrEmpty(q)));
    }

    private void closeSearch() {
        this.navigator.pop();
    }
}
