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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.analytics.Analytics;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.activity.BaseActivityModule;
import com.futurice.freesound.network.api.DefaultFreeSoundSearchService;
import com.futurice.freesound.network.api.FreeSoundApi;
import com.futurice.freesound.network.api.FreeSoundSearchService;

import dagger.Module;
import dagger.Provides;

@Module(includes = BaseActivityModule.class)
class SearchActivityModule {

    @Provides
    @ActivityScope
    static SearchActivityViewModel provideSearchViewModel(SearchDataModel searchDataModel,
                                                          Analytics analytics) {
        return new SearchActivityViewModel(searchDataModel, analytics);
    }

    @Provides
    @ActivityScope
    static SearchDataModel provideSearchDataModel(FreeSoundSearchService freeSoundSearchService) {
        return new DefaultSearchDataModel(freeSoundSearchService);
    }

    @Provides
    static FreeSoundSearchService provideFreeSoundsSearchService(FreeSoundApi freeSoundApi) {
        return new DefaultFreeSoundSearchService(freeSoundApi);
    }
}
