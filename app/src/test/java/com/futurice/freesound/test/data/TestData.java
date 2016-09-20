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

package com.futurice.freesound.test.data;

import com.futurice.freesound.common.InstantiationForbiddenError;
import com.futurice.freesound.network.api.model.GeoLocation;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundImageFormat;
import com.futurice.freesound.network.api.model.SoundSearchResult;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TestData {

    @NonNull
    public static SoundSearchResult searchResult(int count) {
        return SoundSearchResult.builder()
                                .count(count * 2)
                                .next("nextUrl")
                                .previous("prevUrl")
                                .results(sounds(count))
                                .build();
    }

    @NonNull
    public static List<Sound> sounds(int count) {
        List<Sound> sounds = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            sounds.add(sound((long) i));
        }
        return sounds;
    }

    @NonNull
    public static Sound sound(Long index) {
        return Sound.builder()
                    .id(index)
                    .url(indexed("url", index))
                    .name(indexed("name", index))
                    .description(indexed("description", index))
                    .username(indexed("username", index))
                    .tags(tags(index, (int) (index % 5)))
                    .geotag(geotag(index))
                    .images(images())
                    .build();
    }

    @NonNull
    public static List<String> tags(long index, int count) {
        List<String> tags = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tags.add(indexed("tag", index));
        }
        return tags;
    }

    @NonNull
    public static GeoLocation geotag(long index) {
        return GeoLocation.builder()
                          .latitude((double) index)
                          .longitude((double) index + 1)
                          .build();
    }

    @NonNull
    public static Map<SoundImageFormat, String> images() {
        Map<SoundImageFormat, String> images = new HashMap<>();
        for (SoundImageFormat format : SoundImageFormat.values()) {
            images.put(format, "url" + format);
        }
        return images;
    }

    @NonNull
    private static String indexed(@NonNull final String base,
                                  final long index) {
        return String.format(base + "%d", index);
    }

    private TestData() {
        throw new InstantiationForbiddenError();
    }
}
