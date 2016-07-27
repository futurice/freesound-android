package com.futurice.freesound.test.utils;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.observers.TestSubscriber;

import static com.futurice.freesound.utils.Preconditions.get;

public final class TestSubscriberUtils {

    @NonNull
    public static <T> TestSubscriber<T> testSubscribe(@NonNull final Observable<T> observable) {
        TestSubscriber<T> ts = new TestSubscriber<>();
        get(observable).subscribe(ts);
        return ts;
    }

    public TestSubscriberUtils() {
        throw new AssertionError("No instances allowed");
    }
}
