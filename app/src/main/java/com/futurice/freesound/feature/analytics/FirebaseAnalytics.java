package com.futurice.freesound.feature.analytics;

import com.futurice.freesound.inject.app.ForApplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import static com.futurice.freesound.utils.Preconditions.get;

final class FirebaseAnalytics implements Analytics {

    private static final String SINGLE_TEST_EVENT = "SingleEvent";

    @NonNull
    private final com.google.firebase.analytics.FirebaseAnalytics firebaseAnalytics;

    @Inject
    FirebaseAnalytics(@ForApplication @NonNull final Context context) {
        firebaseAnalytics =
                com.google.firebase.analytics.FirebaseAnalytics.getInstance(get(context));
    }

    @Override
    public void log(@NonNull final String event) {
        Bundle bundle = new Bundle();
        bundle.putString(SINGLE_TEST_EVENT, get(event));
        firebaseAnalytics.logEvent(SINGLE_TEST_EVENT, bundle);
    }
}
