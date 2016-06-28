package com.futurice.freesound.app;

import com.futurice.freesound.app.module.ApiModule;
import com.futurice.freesound.app.module.DataModule;
import com.futurice.freesound.app.module.ImagesModule;
import com.futurice.freesound.inject.app.BaseApplicationModule;

import dagger.Module;

@Module(includes = {BaseApplicationModule.class,
                    ApiModule.class,
                    ImagesModule.class,
                    DataModule.class})
class FreesoundApplicationModule {
}
