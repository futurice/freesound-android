package com.futurice.freesound.inject.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseFragmentModule {

    private final Fragment fragment;

    public BaseFragmentModule(@NonNull final android.support.v4.app.Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @PerFragment
    Fragment provideFragment() {
        return fragment;
    }

}
