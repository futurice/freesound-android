package com.futurice.freesound.utils;

import android.support.annotation.NonNull;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public final class SubscriptionUtils {

    @NonNull
    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textView::setText);
    }

    private SubscriptionUtils() {
        throw new AssertionError("No instances allowed");
    }
}
