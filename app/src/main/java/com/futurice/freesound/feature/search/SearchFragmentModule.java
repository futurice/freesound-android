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
