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

package com.futurice.freesound.feature.home;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.network.api.FreeSoundApiService;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
class HomeActivityModule {

    @Provides
    @ActivityScope
    static HomeViewModel provideHomeViewModel(Navigator navigator,
                                              SchedulerProvider schedulerProvider) {
        return new HomeViewModel(navigator, schedulerProvider);
    }

    @Provides
    @ActivityScope
    static UserDataModel provideUserDataModel(FreeSoundApiService freeSoundApiService) {
        return new DefaultUserDataModel(freeSoundApiService);
    }

}
