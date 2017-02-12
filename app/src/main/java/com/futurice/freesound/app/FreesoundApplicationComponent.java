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

package com.futurice.freesound.app;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.inject.app.BaseApplicationComponent;
import com.futurice.freesound.inject.app.ForApplication;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.squareup.picasso.Picasso;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = FreesoundApplicationModule.class)
@Singleton
public interface FreesoundApplicationComponent extends BaseApplicationComponent {

    android.app.Application getApplication();

    @ForApplication
    Context getApplicationContext();

    FreeSoundApiService getFreeSoundApiService();

    Picasso getPicasso();

    Analytics getAnalytics();

    SchedulerProvider getSchedulerProvider();

    void inject(final FreesoundApplication application);
}
