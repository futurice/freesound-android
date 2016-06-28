package com.futurice.freesound.inject.app;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = BaseApplicationModule.class)
@Singleton
public interface BaseApplicationComponent {

    android.app.Application getApplication();

    @Application
    Context getApplicationContext();

}
