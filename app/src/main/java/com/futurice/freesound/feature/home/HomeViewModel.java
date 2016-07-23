package com.futurice.freesound.feature.home;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.functional.Unit;
import com.futurice.freesound.viewmodel.BaseViewModel;
import com.jakewharton.rxrelay.PublishRelay;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.futurice.freesound.utils.Preconditions.get;

final class HomeViewModel extends BaseViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();

    @NonNull
    private final Navigator navigator;

    @NonNull
    private final PublishRelay<Unit> openSearch = PublishRelay.create();

    HomeViewModel(@NonNull final Navigator navigator) {
        this.navigator = get(navigator);
    }

    void openSearch() {
        openSearch.call(Unit.DEFAULT);
    }

    @Override
    public void bind(@NonNull final CompositeSubscription subscriptions) {
        openSearch.subscribeOn(AndroidSchedulers.mainThread())
                  .observeOn(Schedulers.computation())
                  .subscribe(__ -> navigator.openSearch(),
                             e -> Log.e(TAG, "Error clearing search", e));

    }
}
