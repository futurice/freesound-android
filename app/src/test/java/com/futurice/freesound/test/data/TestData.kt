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

package com.futurice.freesound.test.data

import com.futurice.freesound.common.InstantiationForbiddenError
import com.futurice.freesound.network.api.model.*

class TestData private constructor() {

    init {
        throw InstantiationForbiddenError()
    }

    companion object {

        fun accessToken(): AccessToken {
            return AccessToken("accessToken",
                    "scope",
                    2000L,
                    "refreshToken")
        }

        fun user(): User {
            return User("username", "about", avatar())
        }

        fun avatar(): Avatar {
            return Avatar("http://futurice.com/small.png",
                    "http://futurice.com/medium.png",
                    "http://futurice.com/large.png")
        }

        fun searchResult(count: Int): SoundSearchResult {
            return SoundSearchResult.builder()
                    .count(count * 2)
                    .next("nextUrl")
                    .previous("prevUrl")
                    .results(sounds(count))
                    .build()
        }

        fun sounds(count: Int): List<Sound> {
            return (1L..(count - 1))
                    .map { sound(it) }
                    .toList()
        }

        fun sound(index: Long): Sound {
            return Sound.builder()
                    .id(index)
                    .url("url $index")
                    .name("name $index")
                    .description("description $index")
                    .username("username $index")
                    .tags(tags(index, (index % 5).toInt()))
                    .geotag(geotag(index))
                    .images(images())
                    .previews(previews())
                    .build()
        }

        fun tags(index: Long, count: Int): List<String> {
            return (1..(count - 1))
                    .map { "tag $index" }
                    .toList()
        }

        fun geotag(index: Long): GeoLocation {
            return GeoLocation.builder()
                    .latitude(index.toDouble())
                    .longitude(index.toDouble() + 1)
                    .build()
        }

        fun images(): Sound.Image {
            return Sound.Image.builder()
                    .medSizeWaveformUrl("https://url.com/mw")
                    .largeSizeWaveformUrl("https://url.com/lw")
                    .medSizeSpectralUrl("https://url.com/ms")
                    .largeSizeSpectralUrl("https://url.com/ls")
                    .build()
        }

        fun previews(): Sound.Preview {
            return Sound.Preview.builder()
                    .lowQualityMp3Url("https://url.com/lqmp3")
                    .highQualityMp3Url("https://url.com/hqmp3")
                    .lowQualityOggUrl("https://url.com/lqogg")
                    .highQualityOggUrl("https://url.com/hgogg")
                    .build()
        }
    }

}
