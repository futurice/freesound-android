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

package com.futurice.freesound.feature.common.view;

import com.futurice.freesound.feature.common.WaveformExtractor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

public class WaveformViewTarget implements Target {

    @NonNull
    private final WaveformView waveformView;

    @NonNull
    private final WaveformExtractor waveformExtractor;

    public WaveformViewTarget(@NonNull final WaveformView waveformView,
                              @NonNull final WaveformExtractor waveformExtractor) {
        this.waveformView = get(waveformView);
        this.waveformExtractor = get(waveformExtractor);
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
        float[] waveform = waveformExtractor.extract(bitmap);
        waveformView.setWaveform(waveform);
    }

    @Override
    public void onBitmapFailed(final Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(final Drawable placeHolderDrawable) {
        waveformView.clear();
    }
}
