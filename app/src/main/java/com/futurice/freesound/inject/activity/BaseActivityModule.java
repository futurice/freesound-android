package com.futurice.freesound.inject.activity;

import com.futurice.freesound.feature.common.DefaultNavigator;
import com.futurice.freesound.feature.common.Navigator;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class BaseActivityModule {

    private final android.app.Activity activity;

    public BaseActivityModule(@NonNull final android.app.Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    @PerActivity
    android.app.Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    Navigator provideNavigator(Activity activity) {
        return new DefaultNavigator(activity);
    }

}
