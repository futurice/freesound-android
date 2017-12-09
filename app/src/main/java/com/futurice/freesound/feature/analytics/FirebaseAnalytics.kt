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

package com.futurice.freesound.feature.analytics

import android.content.Context
import android.os.Bundle
import com.futurice.freesound.inject.app.ForApplication
import javax.inject.Inject

internal class FirebaseAnalytics @Inject
constructor(@ForApplication context: Context) : Analytics {

    private val firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics =
            com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)

    override fun log(event: String) {
        val bundle = Bundle().apply {
            putString(SINGLE_TEST_EVENT, event)
        }
        firebaseAnalytics.logEvent(SINGLE_TEST_EVENT, bundle)
    }

    companion object {

        private const val SINGLE_TEST_EVENT = "SingleEvent"
    }
}
