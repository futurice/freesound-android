package com.futurice.freesound.viewmodel;

import android.support.annotation.NonNull;

import rx.subscriptions.CompositeSubscription;

/**
 * ViewModel that doesn't have a data connection.
 */
public class SimpleViewModel extends BaseViewModel {

    @Override
    public final void bind(@NonNull final CompositeSubscription subscriptions) {
        // Nothing - has no data source to bind to.
    }
}
