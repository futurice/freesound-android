package com.futurice.freesound.feature.search;

import com.futurice.freesound.inject.fragment.BaseFragmentComponent;
import com.futurice.freesound.inject.fragment.FragmentScope;

import dagger.Component;

@FragmentScope
@Component(dependencies = SearchActivityComponent.class, modules = SearchFragmentModule.class)
public interface SearchFragmentComponent extends BaseFragmentComponent {

    SoundItemAdapter getSoundItemAdapter();

    void inject(final SearchFragment searchFragment);
}
