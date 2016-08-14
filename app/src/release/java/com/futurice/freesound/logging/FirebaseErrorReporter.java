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

package com.futurice.freesound.logging;

import com.google.firebase.crash.FirebaseCrash;

import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

/**
 * A {@Link ErrorReporter} that reports exceptions via Firebase.
 */
class FirebaseErrorReporter implements ErrorReporter {

    @Override
    public void report(@NonNull final Throwable throwable) {
        FirebaseCrash.report(get(throwable));
    }
}
