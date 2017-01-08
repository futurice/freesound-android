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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.core.adapter.AdapterInteractor;
import com.futurice.freesound.core.adapter.SimpleAdapterInteractor;
import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseFragmentModule.class)
class SearchFragmentModule {

    @Provides
    @FragmentScope
    static SearchFragmentViewModel provideSearchFragmentViewModel(SearchDataModel searchDataModel,
                                                                  Navigator navigator,
                                                                  AudioPlayer audioPlayer) {
        return new SearchFragmentViewModel(searchDataModel, navigator, audioPlayer);
    }

    @Provides
    @FragmentScope
    static SoundItemAdapter provideSoundItemAdapter(
            AdapterInteractor<DisplayableItem> adapterInteractor,
            Picasso picasso,
            SoundItemViewModelFactory viewModelFactory) {
        return new SoundItemAdapter(adapterInteractor, picasso, viewModelFactory);
    }

    @Provides
    @FragmentScope
    static AdapterInteractor<DisplayableItem> provideAdapterInteractor() {
        return new SimpleAdapterInteractor<>();
    }
}
