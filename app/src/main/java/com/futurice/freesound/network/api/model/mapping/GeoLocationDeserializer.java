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