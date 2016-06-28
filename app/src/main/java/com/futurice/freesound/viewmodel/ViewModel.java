package com.futurice.freesound.viewmodel;

import android.support.annotation.NonNull;

import rx.subscriptions.CompositeSubscription;

public interface ViewModel {

    void bindToDataModel();

    void unbindDataModel();

    // TODO This should be protected ideally
    void bind(@NonNull final CompositeSubscription subscriptions);

    void destroy();
}
