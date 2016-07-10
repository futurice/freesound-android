package com.futurice.freesound.test.data;

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
        throw new AssertionError("No instances allowed");
    }
}
