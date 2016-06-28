package com.futurice.freesound.viewmodel;

import android.support.annotation.NonNull;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseViewModel implements ViewModel {

    private final CompositeSubscription dataSubscription = new CompositeSubscription();

    public final void bindToDataModel() {
        bind(dataSubscription);
    }

    public final void unbindDataModel() {
        dataSubscription.clear();
    }

    public abstract void bind(@NonNull final CompositeSubscription subscriptions);

    public final void destroy() {
        dataSubscription.clear(); // TODO Needed?
        dataSubscription.unsubscribe();
    }
}
