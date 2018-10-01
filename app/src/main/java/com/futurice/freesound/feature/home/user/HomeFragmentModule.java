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

import com.futurice.freesound.arch.mvi.Logger;
import com.futurice.freesound.arch.mvi.store.Store;
import com.futurice.freesound.arch.mvi.view.UiBinder;
import com.futurice.freesound.arch.mvi.viewmodel.BaseViewModel;
import com.futurice.freesound.arch.mvi.viewmodel.UglyViewModelProviderBridgeKt;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.feature.user.UserRepository;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;

import dagger.Module;
import dagger.Provides;
import io.reactivex.FlowableTransformer;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@Module(includes = BaseFragmentModule.class)
public class HomeFragmentModule {

    private final HomeFragment homeFragment;

    HomeFragmentModule(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Provides
    static Logger provideLogger() {
        return new Logger();
    }

    @Provides
    static BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel> provideHomeFragmentViewModel(
            android.support.v4.app.Fragment fragment,
            Function0<BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>> provider) {
        // No explicit scoping: let the Factory determine the scoping.
        return UglyViewModelProviderBridgeKt.createViewModel(fragment, provider);
    }

    @Provides
    static Function0<BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>> providerHomeFragmentViewModelProvider(
            Store<HomeUiAction, HomeUiResult, HomeUiModel> store,
            SchedulerProvider schedulerProvider,
            Logger logger) {
        return () -> new BaseViewModel<>(HomeUserUiKt.getINITIAL_UI_EVENT(),
                HomeUserUiKt.getEventMapper(),
                store,
                schedulerProvider,
                HomeUserUiKt.LOG_TAG,
                logger);
    }

    @Provides
    static Function2<HomeUiModel, HomeUiResult, HomeUiModel> provideHomeFragmentReducer() {
        return HomeUserUiReducerKt.getReducer();
    }

    @Provides
    static Store<HomeUiAction, HomeUiResult, HomeUiModel> provideHomeFragmentStore(FlowableTransformer<? super HomeUiAction, ? extends HomeUiResult> actionTransformer,
                                                                                   Function2<HomeUiModel, HomeUiResult, HomeUiModel> reducer,
                                                                                   Logger logger) {
        return new Store<>(
                HomeUserUiKt.getINITIAL_UI_STATE(),
                actionTransformer,
                reducer,
                HomeUserUiKt.LOG_TAG,
                logger);
    }

    @Provides
    static FlowableTransformer<? super HomeUiAction, ? extends HomeUiResult> provideHomeFragmentViewModelActionTransformer(HomeUserInteractor homeUserInteractor, RefreshInteractor refreshInteractor) {
        return HomeUserUiKt.actionTransformer(homeUserInteractor, refreshInteractor);
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
    UiBinder<HomeUiEvent, HomeUiModel> provideUiBinder(BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel> viewModel) {
        return new UiBinder<>(homeFragment, viewModel, homeFragment);
    }

}
