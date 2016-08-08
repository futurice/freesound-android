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

package com.futurice.freesound.feature.details;

import com.futurice.freesound.inject.activity.ForActivity;
import com.futurice.freesound.network.api.model.Sound;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import static com.futurice.freesound.utils.Preconditions.get;

public class DetailsActivity extends AppCompatActivity {

    private static final String SOUND_PARAM = "sound";

    public static void open(@NonNull @ForActivity final Context context,
                            @NonNull final Sound sound) {
        Intent intent = new Intent(get(context), DetailsActivity.class);
        intent.putExtra(SOUND_PARAM, get(sound));
        context.startActivity(intent);
    }
}
