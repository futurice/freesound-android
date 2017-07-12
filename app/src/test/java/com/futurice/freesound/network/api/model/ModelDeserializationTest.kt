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

import com.futurice.freesound.network.api.ApiConstants
import com.futurice.freesound.network.api.model.mapping.GeoLocationDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ModelDeserializationTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder().setDateFormat(ApiConstants.DATE_FORMAT_PATTERN)
                .registerTypeAdapterFactory(FreesoundTypeAdapterFactory.create())
                .registerTypeAdapter(GeoLocation::class.java, GeoLocationDeserializer())
                .create()
    }

    @Test
    fun deserialize_sound_search_result() {
        val json = readFile("/sound_search_result.json")
        val actualSoundSearchResult = gson.fromJson(json, SoundSearchResult::class.java)

        assertThat(actualSoundSearchResult).isEqualTo(expectedSoundSearchResult)
    }

    private fun readFile(filename: String): String {
        return this::class.java.getResource(filename).readText()
    }

    private fun toDate(date: String): Date {
        return SimpleDateFormat(ApiConstants.DATE_FORMAT_PATTERN, Locale.ENGLISH).parse(date)
    }

    private val expectedSoundSearchResult = SoundSearchResult.builder()
            .count(157008)
            .next("http://freesound.org/apiv2/search/text/?&query=sound&page=2&fields=geotag,images,description,username,id,url,name,duration,previews,tags,created")
            .results(mutableListOf(
                    Sound.builder()
                            .id(360940)
                            .url("https://freesound.org/people/InspectorJ/sounds/360940/")
                            .name("Worst Sound in the World Contest, E.wav")
                            .tags(mutableListOf("scanner",
                                    "dontlistentothissound",
                                    "Annoying",
                                    "worst",
                                    "Earbleed",
                                    "Horrible",
                                    "Worst",
                                    "earbleed",
                                    "terrible",
                                    "Terrible",
                                    "horrible",
                                    "Scanner",
                                    "annoying",
                                    "sound",
                                    "Sound"))
                            .description("A horrible sound for a contest, created by recording an old photo-slide scanner, paulstretching the sound six times, layering it with adjacent semitone pitches, and finally adding a very deep tremolo. An awful sound.\r\n\r\nPlease comment on where you intend to use the sound, and feel free to post a link to the work where you used it if you want (I enjoy watching/listening to anything you create!)\r\n\r\nThe sound was recorded using a \"H1 Zoom V2 recorder\".\r\n\r\nOriginally edited using Audacity and Paulstretch on 25th September 2016.\r\n\r\nNote: Audio quality is always better when downloaded.")
                            .created(toDate("2016-09-27T01:47:34.279164"))
                            .duration(9.79125f)
                            .username("InspectorJ")
                            .previews(Sound.Preview.builder()
                                    .lowQualityOggUrl("https://freesound.org/data/previews/360/360940_5121236-lq.ogg")
                                    .lowQualityMp3Url("https://freesound.org/data/previews/360/360940_5121236-lq.mp3")
                                    .highQualityOggUrl("https://freesound.org/data/previews/360/360940_5121236-hq.ogg")
                                    .highQualityMp3Url("https://freesound.org/data/previews/360/360940_5121236-hq.mp3")
                                    .build())
                            .images(Sound.Image.builder()
                                    .largeSizeWaveformUrl("https://freesound.org/data/displays/360/360940_5121236_wave_L.png")
                                    .medSizeWaveformUrl("https://freesound.org/data/displays/360/360940_5121236_wave_M.png")
                                    .medSizeSpectralUrl("https://freesound.org/data/displays/360/360940_5121236_spec_M.jpg")
                                    .largeSizeSpectralUrl("https://freesound.org/data/displays/360/360940_5121236_spec_L.jpg")
                                    .build())
                            .build(),
                    Sound.builder()
                            .id(360937)
                            .url("https://freesound.org/people/InspectorJ/sounds/360937/")
                            .name("Worst Sound in the World Contest, C.wav")
                            .tags(mutableListOf("dontlistentothissound",
                                    "Annoying",
                                    "worst",
                                    "Earbleed",
                                    "Horrible",
                                    "Worst",
                                    "Noodle",
                                    "earbleed",
                                    "terrible",
                                    "Terrible",
                                    "horrible",
                                    "noodle",
                                    "annoying",
                                    "sound",
                                    "Sound"))
                            .description("(WARNING HEADPHONE USERS!)\r\n\r\nA horrible sound for a contest, created by scraping a swimming noodle and then phasing it with 100% feedback (making the original noodle sound irrelevant). This phasing was then layered on top of itself.\r\n\r\nPlease comment on where you intend to use the sound, and feel free to post a link to the work where you used it if you want (I enjoy watching/listening to anything you create!)\r\n\r\nThe sound was recorded using a \"H1 Zoom V2 recorder\".\r\n\r\nOriginally edited using Audacity and Paulstretch on 25th September 2016.\r\n\r\nNote: Audio quality is always better when downloaded.")
                            .geotag(GeoLocation.builder()
                                    .latitude(4.65241157574)
                                    .longitude(-74.0594869852)
                                    .build())
                            .created(toDate("2016-09-27T01:47:32.575865"))
                            .duration(27.839f)
                            .username("InspectorJ")
                            .previews(Sound.Preview.builder()
                                    .lowQualityOggUrl("https://freesound.org/data/previews/360/360937_5121236-lq.ogg")
                                    .lowQualityMp3Url("https://freesound.org/data/previews/360/360937_5121236-lq.mp3")
                                    .highQualityOggUrl("https://freesound.org/data/previews/360/360937_5121236-hq.ogg")
                                    .highQualityMp3Url("https://freesound.org/data/previews/360/360937_5121236-hq.mp3")
                                    .build())
                            .images(Sound.Image.builder()
                                    .largeSizeWaveformUrl("https://freesound.org/data/displays/360/360937_5121236_wave_L.png")
                                    .medSizeWaveformUrl("https://freesound.org/data/displays/360/360937_5121236_wave_M.png")
                                    .medSizeSpectralUrl("https://freesound.org/data/displays/360/360937_5121236_spec_M.jpg")
                                    .largeSizeSpectralUrl("https://freesound.org/data/displays/360/360937_5121236_spec_L.jpg")
                                    .build())
                            .build()
            ))
            .build()

}