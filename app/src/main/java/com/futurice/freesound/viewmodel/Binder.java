package com.futurice.freesound.viewmodel;

import rx.subscriptions.CompositeSubscription;

public interface Binder {

    void bind(final CompositeSubscription subscriptions);

    void unbind();

}

