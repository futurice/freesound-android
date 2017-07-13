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

package com.futurice.freesound.network.api.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import java.util.*

/**
 * Refer to: http://www.freesound.org/docs/api/resources_apiv2.html#sound-resources
 */
data class Sound(

    // The sound’s unique identifier.
    val id: Long?,

    // The URI for this sound on the Freesound website.
    val url: String?,

    // The name user gave to the sound.
    val name: String?,

    // An array of tags the user gave to the sound.
    val tags: List<String>?,

    // The description the user gave to the sound.
    val description: String?,

    // Latitude and longitude of the geotag separated by spaces
    // (e.g. “41.0082325664 28.9731252193”, only for sounds that have been geotagged).
    val geotag: GeoLocation?,

    // The username of the uploader of the sound.
    val username: String?,

    // Thumbnail image URLs of the waveform/spectral plot
    val images: Image,

    // Preview sounds URLs
    val previews: Preview?,

    // Duration in seconds
    val duration: Float?,

    val created: Date?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readParcelable(GeoLocation::class.java.classLoader),
        parcel.readString(),
        parcel.readParcelable(Image::class.java.classLoader),
        parcel.readParcelable(Preview::class.java.classLoader),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readSerializable() as? Date)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(url)
        parcel.writeString(name)
        parcel.writeStringList(tags)
        parcel.writeString(description)
        parcel.writeParcelable(geotag, flags)
        parcel.writeString(username)
        parcel.writeParcelable(images, flags)
        parcel.writeParcelable(previews, flags)
        parcel.writeValue(duration)
        parcel.writeSerializable(created)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sound> {
        override fun createFromParcel(parcel: Parcel): Sound {
            return Sound(parcel)
        }

        override fun newArray(size: Int): Array<Sound?> {
            return arrayOfNulls(size)
        }
    }
}

data class Image(
    @Json(name = "waveform_m")
    val medSizeWaveformUrl: String,

    @Json(name = "waveform_l")
    val largeSizeWaveformUrl: String,

    @Json(name = "spectral_m")
    val medSizeSpectralUrl: String,

    @Json(name = "spectral_l")
    val largeSizeSpectralUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(medSizeWaveformUrl)
        parcel.writeString(largeSizeWaveformUrl)
        parcel.writeString(medSizeSpectralUrl)
        parcel.writeString(largeSizeSpectralUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}

data class Preview(
    @Json(name = "preview-lq-mp3")
    val lowQualityMp3Url: String?,

    @Json(name = "preview-hq-mp3")
    val highQualityMp3Url: String?,

    @Json(name = "preview-lq-ogg")
    val lowQualityOggUrl: String?,

    @Json(name = "preview-hq-ogg")
    val highQualityOggUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lowQualityMp3Url)
        parcel.writeString(highQualityMp3Url)
        parcel.writeString(lowQualityOggUrl)
        parcel.writeString(highQualityOggUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Preview> {
        override fun createFromParcel(parcel: Parcel): Preview {
            return Preview(parcel)
        }

        override fun newArray(size: Int): Array<Preview?> {
            return arrayOfNulls(size)
        }
    }
}