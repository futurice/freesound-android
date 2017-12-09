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

package com.futurice.freesound.feature.details

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.futurice.freesound.inject.activity.ForActivity
import com.futurice.freesound.network.api.model.Sound

class DetailsActivity : AppCompatActivity() {
    companion object {

        private val SOUND_PARAM = "sound"

        fun open(@ForActivity context: Context, sound: Sound) {
            val intent = Intent(context, DetailsActivity::class.java).apply {
                putExtra(SOUND_PARAM, sound)
            }
            context.startActivity(intent)
        }
    }
}
