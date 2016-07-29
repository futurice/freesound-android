package com.futurice.freesound.feature.common;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.futurice.freesound.inject.app.Application;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

public class DefaultAnalytics implements Analytics {

    private static final String SINGLE_TEST_EVENT = "SingleEvent";

    @NonNull
    private final FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    public DefaultAnalytics(@Application @NonNull final Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void log(@NonNull final String event) {
        Bundle bundle = new Bundle();
        bundle.putString(SINGLE_TEST_EVENT, event);
        mFirebaseAnalytics.logEvent(SINGLE_TEST_EVENT, bundle);
    }
}
