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
