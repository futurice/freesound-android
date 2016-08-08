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

package com.futurice.freesound.feature.common;

import com.futurice.freesound.feature.search.SearchActivity;
import com.futurice.freesound.network.api.model.Sound;

import android.app.Activity;
import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

public final class DefaultNavigator implements Navigator {

    @NonNull
    private final Activity activity;

    public DefaultNavigator(@NonNull final Activity activity) {
        this.activity = get(activity);
    }

    @Override
    public void openSearch() {
        SearchActivity.open(activity);
    }

    @Override
    public void openSoundDetails(@NonNull final Sound sound) {
        // TODO Via the DetailsActivity.open
    }

}
