package com.futurice.freesound.inject.fragment;

import com.futurice.freesound.inject.activity.BaseActivityModule;

import dagger.Component;

@FragmentScope
@Component(dependencies = BaseActivityModule.class, modules = BaseFragmentModule.class)
public interface BaseFragmentComponent {

    @FragmentScope
    android.support.v4.app.Fragment getFragment();

}
