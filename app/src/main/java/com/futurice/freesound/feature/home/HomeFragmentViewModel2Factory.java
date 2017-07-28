/*
 * Copyright 2017 Futurice GmbH
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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

class HomeFragmentViewModel2Factory implements ViewModelProvider.Factory {

    private final HomeFragmentDataEvents homeFragmentDataEvents;
    private final SchedulerProvider schedulerProvider;

    HomeFragmentViewModel2Factory(HomeFragmentDataEvents homeFragmentDataEvents,
                                  SchedulerProvider schedulerProvider) {
        this.homeFragmentDataEvents = homeFragmentDataEvents;
        this.schedulerProvider = schedulerProvider;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeFragmentViewModel2.class)) {
            return (T) new HomeFragmentViewModel2(homeFragmentDataEvents.dataEvents(),
                                                  schedulerProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
