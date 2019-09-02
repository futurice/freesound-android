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

package com.futurice.freesound.feature.home.user;

import com.futurice.freesound.arch.mvi.LoggingTransitionObserver;
import com.futurice.freesound.arch.mvi.view.Binder;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.feature.user.UserRepository;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import kotlin.jvm.functions.Function2;

@Module(includes = BaseFragmentModule.class)
public class HomeFragmentModule {

    private final HomeFragment homeFragment;

    HomeFragmentModule(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Provides
    static LoggingTransitionObserver provideLogger() {
        return new LoggingTransitionObserver();
    }

    @Provides
    static HomeFragmentViewModel provideHomeFragmentViewModel(
            android.support.v4.app.Fragment fragment,
            HomeUserInteractor homeUserInteractor,
            RefreshInteractor refreshInteractor,
            SchedulerProvider schedulerProvider,
            LoggingTransitionObserver loggingTransitionObserver) {

        return ViewModelProviders.of(fragment, new ViewModelProvider.Factory() {
            @SuppressWarnings("unchecked")
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
                return (T) new HomeFragmentViewModel(homeUserInteractor,
                                                     refreshInteractor,
                                                     schedulerProvider,
                                                     loggingTransitionObserver);
            }
        }).get(HomeFragmentViewModel.class);
    }

    @Provides
    static Function2<HomeUiModel, HomeUiResult, HomeUiModel> provideHomeFragmentReducer() {
        return HomeUserUiReducerKt.getReducer();
    }

    @Provides
    static HomeUserInteractor provideHomeUserInteractor(UserRepository userRepository) {
        return new HomeUserInteractor(userRepository);
    }

    @Provides
    static RefreshInteractor provideRefreshInteractor(UserRepository userRepository) {
        return new RefreshInteractor(userRepository);
    }

    @Provides
    @FragmentScope
    Binder<HomeUiEvent, HomeUiModel, HomeFragmentViewModel> provideFlow(
            HomeFragmentViewModel viewModel) {
        return new Binder<>(homeFragment, viewModel, homeFragment);
    }

}
