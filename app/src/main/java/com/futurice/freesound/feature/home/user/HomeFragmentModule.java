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
import com.futurice.freesound.arch.mvi.view.Flow;
import com.futurice.freesound.arch.mvi.viewmodel.BaseViewModel;
import com.futurice.freesound.arch.mvi.viewmodel.UglyViewModelProviderBridgeKt;
import com.futurice.freesound.arch.mvi.viewmodel.MviViewModel;
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
    static HomeFragmentViewModel provideHomeFragmentViewModel(
            android.support.v4.app.Fragment fragment,
            Function0<MviViewModel<HomeUiEvent, HomeUiModel>> provider) {
        // No explicit scoping: let the Factory determine the scoping.
        return UglyViewModelProviderBridgeKt.createViewModel(fragment, provider);
    }

    @Provides
    static Function0<MviViewModel<HomeUiEvent, HomeUiModel>> providerHomeFragmentViewModelProvider(
            FlowableTransformer<? super HomeUiAction, ? extends HomeUiResult> dispatcher,
            Store<HomeUiResult, HomeUiModel> store,
            SchedulerProvider schedulerProvider,
            Logger logger) {
        return () -> new HomeFragmentViewModel(new BaseViewModel<>(HomeFragmentViewModelKt.getINITIAL_UI_EVENT(),
                HomeFragmentViewModelKt.getEventMapper(),
                dispatcher,
                store,
                schedulerProvider,
                HomeFragmentViewModelKt.LOG_TAG,
                logger));
    }

    @Provides
    static Function2<HomeUiModel, HomeUiResult, HomeUiModel> provideHomeFragmentReducer() {
        return HomeUserUiReducerKt.getReducer();
    }

    @Provides
    static Store<HomeUiResult, HomeUiModel> provideHomeFragmentStore(Function2<HomeUiModel, HomeUiResult, HomeUiModel> reducer,
                                                                     Logger logger) {
        return new Store<>(
                HomeFragmentViewModelKt.getINITIAL_UI_STATE(),
                reducer,
                HomeFragmentViewModelKt.LOG_TAG,
                logger);
    }

    @Provides
    static FlowableTransformer<? super HomeUiAction, ? extends HomeUiResult> provideHomeFragmentViewModelDispatcher(HomeUserInteractor homeUserInteractor, RefreshInteractor refreshInteractor) {
        return HomeFragmentViewModelKt.dispatcher(homeUserInteractor, refreshInteractor);
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
    Flow<HomeUiEvent, HomeUiModel, MviViewModel<HomeUiEvent, HomeUiModel>> provideFlow(HomeFragmentViewModel viewModel) {
        return new Flow<>(homeFragment, viewModel, homeFragment);
    }

}
