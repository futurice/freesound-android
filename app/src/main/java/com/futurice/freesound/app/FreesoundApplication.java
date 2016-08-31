/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.app;

import com.facebook.stetho.Stetho;
import com.futurice.freesound.app.module.ApiModule;
import com.futurice.freesound.app.module.ImagesModule;
import com.futurice.freesound.core.BaseApplication;
import com.futurice.freesound.inject.app.BaseApplicationModule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import timber.log.Timber;

import static com.futurice.freesound.utils.Preconditions.get;

public class FreesoundApplication extends BaseApplication<FreesoundApplicationComponent> {

    @Inject
    @Nullable
    Timber.Tree loggingTree;

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
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

    private void initialize() {
        initLogging();
        initStetho();
    }

    private void initLogging() {
        Timber.uprootAll();
        Timber.plant(get(loggingTree));
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                      .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                      .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                      .build());
    }
}
