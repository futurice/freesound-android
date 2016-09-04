package com.futurice.freesound.test.rx;

import android.support.annotation.NonNull;

import rx.Observer;

/**
 * An {@link Observer} which swallows all events without side effects.
 */
public class IgnoringObserver<T> implements Observer<T> {

    @NonNull
    public static <T> Observer<T> create() {
        return new IgnoringObserver<>();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(final Throwable e) {

    }

    @Override
    public void onNext(final T t) {

    }
}
