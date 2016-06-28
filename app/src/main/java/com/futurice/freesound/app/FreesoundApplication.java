package com.futurice.freesound.app;

import com.facebook.stetho.Stetho;
import com.futurice.freesound.app.module.ApiModule;
import com.futurice.freesound.app.module.ImagesModule;
import com.futurice.freesound.core.BaseApplication;
import com.futurice.freesound.inject.app.BaseApplicationModule;

import android.support.annotation.NonNull;

public class FreesoundApplication extends BaseApplication<FreesoundApplicationComponent> {

    @Override
    public void onCreate() {
        super.onCreate();
        initStetho();
    }

    @Override
    public void inject() {
        component().inject(this);
    }

    @NonNull
    protected FreesoundApplicationComponent createComponent() {
        return DaggerFreesoundApplicationComponent.builder()
                                                  .baseApplicationModule(
                                                       new BaseApplicationModule(this))
                                                  .apiModule(new ApiModule())
                                                  .imagesModule(new ImagesModule())
                                                  .build();
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                      .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                      .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                      .build());
    }
}
