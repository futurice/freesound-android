/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
