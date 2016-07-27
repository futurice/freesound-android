package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.functional.StringFunctions;
import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.utils.TextUtils;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.BehaviorRelay;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import polanski.option.Option;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.functional.Functions.nothing1;
import static com.futurice.freesound.utils.Preconditions.get;

final class SearchViewModel extends BaseViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();

    static final String NO_SEARCH = "";

    @NonNull
    private final SearchDataModel searchDataModel;

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final BehaviorRelay<String> searchTermRelay = BehaviorRelay.create(NO_SEARCH);

    SearchViewModel(@NonNull final SearchDataModel searchDataModel,
                    @NonNull final Navigator navigator) {
        this.searchDataModel = get(searchDataModel);
        this.navigator = get(navigator);
    }

    @NonNull
    Observable<Boolean> getClearButtonVisibleStream() {
        return searchTermRelay.asObservable()
                              .map(SearchViewModel::isCloseEnabled);

    }

    void search(@NonNull final String query) {
        searchTermRelay.call(query);
    }

    @NonNull
    Observable<Option<List<Sound>>> getSounds() {
        return searchDataModel.getSearchResults();
    }

    void openSoundDetails(@NonNull final Sound sound) {
        navigator.openSoundDetails(get(sound));
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        searchTermRelay.subscribeOn(Schedulers.computation())
                       .map(String::trim)
                       .switchMap(this::searchOrClear)
                       .subscribe(nothing1(),
                                  e -> Log.e(TAG, "Error when setting search term", e));

    }

    private Observable<Unit> searchOrClear(@NonNull final String s) {
        return TextUtils.isNullOrEmpty(s) ?
                searchDataModel.clear() :
                Observable.just(s)
                          .filter(StringFunctions.isNotEmpty())
                          .debounce(2, TimeUnit.SECONDS)
                          .switchMap(searchDataModel::querySearch);
    }

    private static boolean isCloseEnabled(@NonNull final String query) {
        return TextUtils.isNotNullOrEmpty(query);
    }

}
