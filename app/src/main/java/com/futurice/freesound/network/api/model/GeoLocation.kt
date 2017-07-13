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
import com.google.auto.value.AutoValue

import android.os.Parcelable

// Using https://github.com/nekocode/android-parcelable-intellij-plugin-kotlin for Parcelable
data class GeoLocation(
        val latitude: Double,
        val longitude: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GeoLocation> {
        override fun createFromParcel(parcel: Parcel): GeoLocation = GeoLocation(parcel)

        override fun newArray(size: Int): Array<GeoLocation?> = arrayOfNulls(size)
    }
}
