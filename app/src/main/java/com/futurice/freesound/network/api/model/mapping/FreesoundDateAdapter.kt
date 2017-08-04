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

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Rfc3339DateJsonAdapter
import com.squareup.moshi.ToJson
import java.util.*

/**
 * Required as the Freesound API uses RFC-3339, *except* that it omits the "Z" suffix!
 * This adds that suffix to make it RFC-3339 compliant and then delegates to the actual
 * Rfc3339DateJsonAdapter.
 */
class FreesoundDateAdapter(private val delegate: Rfc3339DateJsonAdapter) {

    @FromJson fun fromJson(json: String): Date {
        val parsableDate = if (json.endsWith("Z")) json else json.plus("Z")
        return delegate.fromJson("\"$parsableDate\"")
                ?: throw JsonDataException("Unable to deserialize Date from: $json")
    }

    @ToJson fun toJson(date: Date): String {
        return delegate.toJson(date)
                .removePrefix("\"")
                .removeSuffix("Z\"")
    }

}
