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
import com.futurice.freesound.arch.core.BaseApplication;
import com.futurice.freesound.inject.app.BaseApplicationModule;
import com.squareup.leakcanary.LeakCanary;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import timber.log.Timber;

public class FreesoundApplication extends BaseApplication<FreesoundApplicationComponent> {

    @Inject
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

    @Override
    @NonNull
    protected FreesoundApplicationComponent createComponent() {
        return DaggerFreesoundApplicationComponent.builder()
                                                  .baseApplicationModule(
                                                          new BaseApplicationModule(this))
                                                  .build();
    }

    private void initialize() {
        initLogging();
        initStetho();
        initLeakCanary();
    }

    private void initLogging() {
        Timber.uprootAll();
        Timber.plant(loggingTree);
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                      .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                      .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                      .build());
    }

    private void initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
