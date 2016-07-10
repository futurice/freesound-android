package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class GeoLocation implements Parcelable {

    @NonNull
    public abstract Double latitude();

    @NonNull
    public abstract Double longitude();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder latitude(@NonNull final Double latitude);

        public abstract Builder longitude(@NonNull final Double longitude);

        @NonNull
        public abstract GeoLocation build();
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_GeoLocation.Builder();
    }


}
