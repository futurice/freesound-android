package com.futurice.freesound.utils;

import android.support.annotation.NonNull;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public final class SubscriptionUtils {

    private SubscriptionUtils() {
        throw new AssertionError("No instances allowed");
    }

    @NonNull
    static public Subscription subscribeTextViewText(@NonNull final Observable<String> observable,
                                                     @NonNull final TextView textView) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView.setText(s);
                    }
                });
    }
}
