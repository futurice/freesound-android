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

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseFragmentModule.class)
public class SearchFragmentModule {

    @Provides
    @FragmentScope
    SoundItemAdapter provideSoundItemAdapter(Picasso picasso,
                                             SoundItemViewModel_Factory viewModelFactory) {
        return new SoundItemAdapter(picasso, viewModelFactory);
    }

    @Provides
    @FragmentScope
    SoundItemViewModel_Factory provideSoundItemViewModelFactory(Navigator navigator) {
        return new SoundItemViewModel_Factory(navigator);
    }
}
