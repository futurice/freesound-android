package com.futurice.freesound.app.module;

import com.futurice.freesound.inject.app.ForApplication;
import com.squareup.picasso.Picasso;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ImagesModule {

    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context) {
        return Picasso.with(context);
    }

}
