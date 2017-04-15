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

import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.feature.common.ui.adapter.ItemComparator;
import com.futurice.freesound.feature.common.ui.adapter.RecyclerViewAdapter;
import com.futurice.freesound.feature.common.ui.adapter.ViewHolderBinder;
import com.futurice.freesound.feature.common.ui.adapter.ViewHolderFactory;
import com.futurice.freesound.inject.activity.ForActivity;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.FragmentScope;
import com.squareup.picasso.Picasso;

import android.content.Context;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;

import static com.futurice.freesound.feature.search.SearchConstants.SearchResultListItems.SOUND;

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
    RecyclerViewAdapter provideRecyclerAdapter(ItemComparator itemComparator,
                                               Map<Integer, ViewHolderFactory> factoryMap,
                                               Map<Integer, ViewHolderBinder> binderMap,
                                               SchedulerProvider schedulerProvider) {
        return new RecyclerViewAdapter(itemComparator, factoryMap, binderMap, schedulerProvider);
    }

    @Provides
    ItemComparator provideComparator() {
        return new SearchResultItemComparator();
    }

    @IntoMap
    @IntKey(SOUND)
    @Provides
    ViewHolderFactory provideSoundViewHolderFactory(@ForActivity Context context,
                                                    Picasso picasso,
                                                    SchedulerProvider schedulerProvider) {
        return new SoundItemViewHolder.SoundItemViewHolderFactory(context,
                                                                  picasso,
                                                                  schedulerProvider);
    }

    @IntoMap
    @IntKey(SOUND)
    @Provides
    ViewHolderBinder provideSoundViewHolderBinder(SoundItemViewModelFactory viewModelFactory) {
        return new SoundItemViewHolder.SoundItemViewHolderBinder(viewModelFactory);
    }
}
