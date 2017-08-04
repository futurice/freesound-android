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

package com.futurice.freesound.network.api.model.mapping

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class FreesoundDateAdapterTest {

    private lateinit var adapter: FreesoundDateAdapter

    private val gmtFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .apply { timeZone = TimeZone.getTimeZone("GMT") }

    private fun String.toGmtDate(): Date {
        return gmtFormatter.parse(this)
    }

    @Before
    fun setUp() {
        adapter = FreesoundDateAdapter(Rfc3339DateJsonAdapter())
    }

    @Test
    fun fromJson_deserializes_whenNoZSuffix() {
        val jsonString = "2016-03-28T06:48:29.123"
        val expected = jsonString.plus("Z").toGmtDate()

        val date = adapter.fromJson(jsonString)

        assertThat(date).isEqualTo(expected)
    }

    @Test
    fun fromJson_deserializes_whenZSuffix() {
        val jsonString = "2016-03-28T06:48:29.123Z"
        val expected = jsonString.toGmtDate()

        val date = adapter.fromJson(jsonString)

        assertThat(date).isEqualTo(expected)
    }

    @Test
    fun toJson_removesQuotesAndZSuffix() {
        val expected = "2016-03-28T06:48:29.123"
        val date = expected.toGmtDate()

        val json = adapter.toJson(date)

        assertThat(json).isEqualTo(expected)
    }


    @Test
    fun adapter_serialization_roundtrip() {
        val adapter = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(FreesoundDateAdapter(Rfc3339DateJsonAdapter()))
                .build()
                .adapter(Date::class.java)
        val date = "2016-03-28T06:48:29.123".toGmtDate()

        val result = adapter.fromJson(adapter.toJson(date))

        assertThat(result).isEqualTo(date)
    }

    @Test
    fun adapter_serialization_roundtrip_withDataClass() {
        data class TestData(val date: Date)

        val adapter = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(FreesoundDateAdapter(Rfc3339DateJsonAdapter()))
                .build()
                .adapter(TestData::class.java)
        val testData = TestData("2016-03-28T06:48:29.123".toGmtDate())
        val result = adapter.fromJson(adapter.toJson(testData))

        assertThat(result).isEqualTo(testData)
    }

}
