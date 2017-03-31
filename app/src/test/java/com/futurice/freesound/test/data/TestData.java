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
import com.futurice.freesound.network.api.model.AccessToken;
import com.futurice.freesound.network.api.model.Avatar;
import com.futurice.freesound.network.api.model.GeoLocation;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundSearchResult;
import com.futurice.freesound.network.api.model.User;

import android.support.annotation.NonNull;

import java.util.List;

import ix.Ix;

public final class TestData {

    @NonNull
    public static AccessToken accessToken() {
        return AccessToken.builder()
                          .accessToken("accessToken")
                          .scope("scope")
                          .expiresIn(2000L)
                          .refreshToken("refreshToken")
                          .build();
    }

    @NonNull
    public static User user() {
        return User.builder()
                   .about("about")
                   .avatar(avatar())
                   .username("username")
                   .build();
    }

    @NonNull
    public static Avatar avatar() {
        return Avatar.builder()
                     .small("http://futurice.com/small.png")
                     .medium("http://futurice.com/medium.png")
                     .large("http://futurice.com/large.png")
                     .build();
    }

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
        return Ix.range(1, count)
                 .map(i -> sound((long) i))
                 .toList();
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
                    .previews(previews())
                    .build();
    }

    @NonNull
    public static List<String> tags(long index, int count) {
        return Ix.range(1, count)
                 .map(__ -> indexed("tag", index))
                 .toList();
    }

    @NonNull
    public static GeoLocation geotag(long index) {
        return GeoLocation.builder()
                          .latitude((double) index)
                          .longitude((double) index + 1)
                          .build();
    }

    @NonNull
    public static Sound.Image images() {
        return Sound.Image.builder()
                          .medSizeWaveformUrl("https://url.com/mw")
                          .largeSizeWaveformUrl("https://url.com/lw")
                          .medSizeSpectralUrl("https://url.com/ms")
                          .largeSizeSpectralUrl("https://url.com/ls")
                          .build();
    }

    @NonNull
    public static Sound.Preview previews() {
        return Sound.Preview.builder()
                            .lowQualityMp3Url("https://url.com/lqmp3")
                            .highQualityMp3Url("https://url.com/hqmp3")
                            .lowQualityOggUrl("https://url.com/lqogg")
                            .highQualityOggUrl("https://url.com/hgogg")
                            .build();
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
