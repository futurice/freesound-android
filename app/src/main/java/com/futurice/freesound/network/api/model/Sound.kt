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

import android.annotation.SuppressLint
import android.os.Parcelable
import com.petertackage.jonty.Fieldable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Refer to: http://www.freesound.org/docs/api/resources_apiv2.html#sound-resources
 */
@SuppressLint("ParcelCreator")
@Parcelize
@Fieldable
data class Sound(
        val id: Long,
        // The URI for this sound on the Freesound website.
        val url: String,
        // The name user gave to the sound.
        val name: String,
        // An array of tags the user gave to the sound.
        val tags: List<String>,
        // The description the user gave to the sound.
        val description: String,
        // Latitude and longitude of the geotag separated by spaces
        // (e.g. “41.0082325664 28.9731252193”, only for sounds that have been geotagged).
        val geotag: String?,
        // The username of the uploader of the sound.
        val username: String,
        // Thumbnail image URLs of the waveform/spectral plot
        val images: Image,
        // Preview sounds URLs
        val previews: Preview,
        // Duration in seconds
        val duration: Float,
        val created: Date) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Image(

        @Json(name = "waveform_m")
        val medSizeWaveformUrl: String,

        @Json(name = "waveform_l")
        val largeSizeWaveformUrl: String,

        @Json(name = "spectral_m")
        val medSizeSpectralUrl: String,

        @Json(name = "spectral_l")
        val largeSizeSpectralUrl: String) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Preview(

        @Json(name = "preview-lq-mp3")
        val lowQualityMp3Url: String,

        @Json(name = "preview-hq-mp3")
        val highQualityMp3Url: String,

        @Json(name = "preview-lq-ogg")
        val lowQualityOggUrl: String,

        @Json(name = "preview-hq-ogg")
        val highQualityOggUrl: String) : Parcelable
