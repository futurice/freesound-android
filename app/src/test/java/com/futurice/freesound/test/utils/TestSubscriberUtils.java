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
