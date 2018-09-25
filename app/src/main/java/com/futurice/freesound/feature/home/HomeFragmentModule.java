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

import com.futurice.freesound.feature.user.UserRepository;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;
import com.futurice.freesound.mvi.ActionTransformer;
import com.futurice.freesound.mvi.Logger;
import com.futurice.freesound.mvi.Store;
import com.futurice.freesound.mvi.UiBinder;

import dagger.Module;
import dagger.Provides;
import kotlin.jvm.functions.Function0;

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
            Function0<HomeFragmentViewModel> provider) {
        // No explicit scoping: let the Factory determine the scoping.
        return UglyViewModelProviderBridgeKt.createHomeFragmentViewModel(fragment, provider);
    }

    @Provides
    static Function0<HomeFragmentViewModel> providerHomeFragmentViewModelProvider(
            Store<HomeUiAction, HomeUiResult, HomeUiModel> store,
            Logger logger) {
        return () -> new HomeFragmentViewModel(HomeFragmentViewModel.Companion.getLOG_TAG(), logger, HomeUiEvent.Initial.INSTANCE, store);
    }

    @Provides
    static HomeFragmentReducer provideHomeFragmentReducer() {
        return new HomeFragmentReducer();
    }

    @Provides
    static Store<HomeUiAction, HomeUiResult, HomeUiModel> provideHomeFragmentStore(ActionTransformer<HomeUiAction, HomeUiResult> actionTransformer,
                                                                                   HomeFragmentReducer reducer,
                                                                                   Logger logger) {
        return new Store<>(HomeFragmentViewModel.Companion.getLOG_TAG(),
                logger,
                HomeFragmentViewModel.Companion.getINITIAL_UI_STATE(),
                actionTransformer,
                reducer);
    }

    @Provides
    static ActionTransformer<HomeUiAction, HomeUiResult> provideHomeFragmentViewModelActionTransformer(HomeUserInteractor homeUserInteractor,
                                                                                                       RefreshInteractor refreshInteractor) {
        return new HomeFragmentActionTransformer(homeUserInteractor, refreshInteractor);
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
    UiBinder<HomeUiModel, HomeUiEvent> provideUiBinder(HomeFragmentViewModel viewModel) {
        return new UiBinder<>(homeFragment, viewModel, homeFragment);
    }

}
