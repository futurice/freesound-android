package com.futurice.freesound.inject.activity;

import com.futurice.freesound.app.FreesoundApplicationComponent;

import android.content.Context;

import dagger.Component;

@PerActivity
@Component(dependencies = FreesoundApplicationComponent.class, modules = BaseActivityModule.class)
public interface BaseActivityComponent {

    android.app.Activity getActivity();

    @ForActivity
    Context getActivityContext();
}
