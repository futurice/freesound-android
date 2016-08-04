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

package com.futurice.freesound.network.api.model.mapping;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import com.futurice.freesound.network.api.model.GeoLocation;

import java.lang.reflect.Type;

public final class GeoLocationDeserializer implements JsonDeserializer<GeoLocation> {

    @Override
    public GeoLocation deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
        String text = json.getAsString().trim();
        // Format example: "41.0082325664 28.9731252193"
        String[] latLong = text.split(" ", 2);
        if (latLong.length != 2) {
            throw new JsonParseException(
                    String.format("Unable to deserialize latitude/long values from: %s", text));
        }
        return GeoLocation.builder()
                          .latitude(Double.valueOf((latLong[0])))
                          .longitude(Double.valueOf((latLong[1])))
                          .build();
    }

}