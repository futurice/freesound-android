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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;
import com.futurice.freesound.mvi.Renderer;
import com.futurice.freesound.mvi.UiBinder;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseFragmentModule.class)
public class HomeFragmentModule {

    private final HomeFragment homeFragment;

    HomeFragmentModule(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Provides
    static HomeFragmentDataEvents provideHomeFragmentDataBinder(UserDataModel userDataModel) {
        return new HomeFragmentDataEvents(userDataModel);
    }

    @Provides
    static ViewModelProvider.Factory provideViewModelFactory(HomeFragmentDataEvents dataBinder,
                                                             SchedulerProvider schedulerProvider) {
        return new HomeFragmentViewModel2Factory(dataBinder, schedulerProvider);
    }

    @Provides
    @FragmentScope
    static HomeFragmentViewModel2 provideHomeFragmentViewModel2(android.support.v4.app.Fragment f,
                                                                ViewModelProvider.Factory factory) {

        // This can be scoped as @FragmentScope because that allows the component to memoize the
        // value rather than always querying the ViewModelProviders. The ViewModel's actual scope
        // always exceeds that of the Fragment, so this should be ok.
        return ViewModelProviders.of(f, factory)
                                 .get(HomeFragmentViewModel2.class);
    }

    @Provides
    @FragmentScope
    Renderer<Fragment.UiModel> provideRenderer() {
        return homeFragment;
    }

    @Provides
    @FragmentScope
    static UiBinder<Fragment.UiModel, Fragment.UiEvent> provideUiBinder(
            Renderer<Fragment.UiModel> renderer,
            HomeFragmentViewModel2 viewModel,
            SchedulerProvider schedulerProvider) {
        return new UiBinder<>(renderer, viewModel, schedulerProvider);
    }
}
