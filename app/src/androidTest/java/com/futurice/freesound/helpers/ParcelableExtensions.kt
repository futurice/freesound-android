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

package com.futurice.freesound.helpers

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

inline fun <reified R : Parcelable> R.testParcel(): R {
    val bytes = marshallParcelable(this)
    return unmarshallParcelable(bytes)
}

inline fun <reified R : Parcelable> marshallParcelable(parcelable: R): ByteArray {
    val bundle = Bundle().apply { putParcelable(R::class.java.name, parcelable) }
    return marshall(bundle)
}

fun marshall(bundle: Bundle): ByteArray =
        Parcel.obtain().use {
            it.writeBundle(bundle)
            it.marshall()
        }

inline fun <reified R : Parcelable> unmarshallParcelable(bytes: ByteArray): R = unmarshall(bytes)
        .readBundle()
        .run {
            classLoader = R::class.java.classLoader
            getParcelable(R::class.java.name)
        }

fun unmarshall(bytes: ByteArray): Parcel =
        Parcel.obtain().apply {
            unmarshall(bytes, 0, bytes.size)
            setDataPosition(0)
        }

private fun <T> Parcel.use(block: (Parcel) -> T): T =
        try {
            block(this)
        } finally {
            this.recycle()
        }