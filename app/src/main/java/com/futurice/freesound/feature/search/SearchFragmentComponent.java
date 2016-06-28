package com.futurice.freesound.feature.search;

import com.futurice.freesound.inject.fragment.BaseFragmentComponent;
import com.futurice.freesound.inject.fragment.PerFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = SearchActivityComponent.class, modules = SearchFragmentModule.class)
public interface SearchFragmentComponent extends BaseFragmentComponent {

    SoundItemAdapter getSoundItemAdapter();

    void inject(final SearchFragment searchFragment);
}
