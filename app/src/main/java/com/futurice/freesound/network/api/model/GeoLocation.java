package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;

import android.os.Parcelable;
import android.support.annotation.NonNull;

@AutoValue
public abstract class GeoLocation implements Parcelable {

    @NonNull
    public abstract Double latitude();

    @NonNull
    public abstract Double longitude();

    @NonNull
    public static GeoLocation create(@NonNull final Double latitude,
                                     @NonNull final Double longitude) {
        return new AutoValue_GeoLocation(latitude, longitude);
    }
}
