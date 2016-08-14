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

import android.support.annotation.Nullable;

import polanski.option.Option;
import timber.log.Timber;

/**
 * Timber logging {@link timber.log.Timber.Tree} to be used in release builds.
 */
class FirebaseReleaseTree extends Timber.Tree {

    @Override
    protected void log(final int priority,
                       @Nullable final String tag,
                       @Nullable final String message,
                       @Nullable final Throwable throwable) {
        Option.ofObj(throwable)
              .ifSome(it -> FirebaseCrash.report(throwable));
    }
}
