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

package com.futurice.freesound.common.utils

import android.os.Parcel
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class ParcelsTest {

    @Test
    fun writeAndReadObjectToParcel() {
        val data = Data(true)

        val parcel = Parcel.obtain()
        parcel.writeTypedObjectCompat(data, data.describeContents())
        parcel.setDataPosition(0)

        assertThat(parcel.readTypedObjectCompat(Data.CREATOR)).isEqualTo(data)
    }

    @Test
    fun writeAndReadBoolean() {
        val parcel = Parcel.obtain()
        parcel.writeBoolean(true)
        parcel.setDataPosition(0)

        assertThat(parcel.readBoolean()).isTrue()
    }

    @Test
    fun writeAndReadEnum() {
        val parcel = Parcel.obtain()
        parcel.writeEnum(TestEnum.VAL1)
        parcel.setDataPosition(0)

        val result = parcel.readEnum<TestEnum>()!!

        assertThat(result).isEqualTo(TestEnum.VAL1)
    }

    @Test
    fun readAndWriteNullable_whenNull() {
        val parcel = Parcel.obtain()
        parcel.writeNullable(null as String?, { parcel.writeString(it) })
        parcel.setDataPosition(0)

        val result = parcel.readNullable { readString() }

        assertThat(result).isNull()
    }

    @Test
    fun readAndWriteNullable_whenNonNull() {
        val parcel = Parcel.obtain()
        val nullable: String? = "abc"
        parcel.writeNullable(nullable, { parcel.writeString(it) })
        parcel.setDataPosition(0)

        val result = parcel.readNullable { readString() }

        assertThat(result).isEqualTo("abc")
    }

    @Test
    fun readAndWriteDate() {
        val parcel = Parcel.obtain()
        val date = Date(1234)
        parcel.writeDate(date)
        parcel.setDataPosition(0)

        val result = parcel.readDate()

        assertThat(result).isEqualTo(date)
    }

    @Test
    fun readAndWriteDate_whenNull() {
        val parcel = Parcel.obtain()
        parcel.writeDate(null as Date?)
        parcel.setDataPosition(0)

        val result = parcel.readDate()

        assertThat(result).isNull()
    }

    @Test
    fun readStringList() {
    }

    /**
     * Test types
     */

    private enum class TestEnum { VAL1, VAL2 }

    private data class Data(val value: Boolean) : KParcelable {

        private constructor(parcel: Parcel) : this(value = parcel.readBoolean())

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeBoolean(value)
        }

        companion object {
            @JvmField val CREATOR = parcelableCreator(::Data)
        }
    }

    private data class ComplexData(val value: Data) : KParcelable {

        private constructor(parcel: Parcel) : this(value = parcel.readTypedObjectCompat(Data.CREATOR)!!)

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeTypedObjectCompat(value, flags)
        }

        companion object {
            @JvmField val CREATOR = parcelableCreator(::ComplexData)
        }
    }

}
