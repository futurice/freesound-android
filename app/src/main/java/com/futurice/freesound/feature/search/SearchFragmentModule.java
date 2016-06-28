package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.inject.fragment.BaseFragmentModule;
import com.futurice.freesound.inject.fragment.PerFragment;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseFragmentModule.class)
public class SearchFragmentModule {

    @Provides
    @PerFragment
    SoundItemAdapter provideSoundItemAdapter(Picasso picasso,
                                             SoundItemViewModel_Factory viewModelFactory) {
        return new SoundItemAdapter(picasso, viewModelFactory);
    }

    @Provides
    @PerFragment
    SoundItemViewModel_Factory provideSoundItemViewModelFactory(Navigator navigator) {
        return new SoundItemViewModel_Factory(navigator);
    }
}
