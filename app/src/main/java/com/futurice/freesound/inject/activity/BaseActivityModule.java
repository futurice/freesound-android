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

package com.futurice.freesound.inject.activity;

import com.futurice.freesound.feature.common.DefaultNavigator;
import com.futurice.freesound.feature.common.Navigator;

import android.content.Context;
import androidx.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseActivityModule {

    private final android.app.Activity activity;

    public BaseActivityModule(@NonNull final android.app.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    @ActivityScope
    android.app.Activity provideActivity() {
        return activity;
    }

    @Provides
    @ActivityScope
    static Navigator provideNavigator(android.app.Activity activity) {
        return new DefaultNavigator(activity);
    }

}
