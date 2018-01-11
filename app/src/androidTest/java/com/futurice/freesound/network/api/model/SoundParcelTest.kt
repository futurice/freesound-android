/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.network.api.model

import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.futurice.freesound.helpers.testParcel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class SoundParcelTest {

    @Test
    fun sound_is_parcelable() {
        val images = Image("medWav", "largeWav",
                "medSpec", "largeSpec")
        val preview = Preview("lowMp3", "highMp3",
                "lowOgg", "highOgg")

        val sound = Sound(
                id = 123L,
                url = "url",
                name = "name",
                tags = listOf("abc", "123"),
                description = "desc",
                geotag = GeoLocation(1.0, 2.0),
                username = "username",
                images = images,
                previews = preview,
                duration = 5.0F,
                created = Date(1000)
        )

        sound.testParcel()
                .apply {
                    assertThat(this).isEqualTo(sound)
                    assertThat(this).isNotSameAs(sound)
                }
    }


}
