package com.futurice.freesound.inject.fragment;

import com.futurice.freesound.inject.activity.BaseActivityModule;

import dagger.Component;

@PerFragment
@Component(dependencies = BaseActivityModule.class, modules = BaseFragmentModule.class)
public interface BaseFragmentComponent {

    @PerFragment
    android.support.v4.app.Fragment getFragment();

}
