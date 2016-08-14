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

package com.futurice.freesound.feature.common;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import timber.log.Timber;

public abstract class WaveformExtractor {

    public final float[] extract(@NonNull final Bitmap bitmap) {
        final long debugStartTime = System.currentTimeMillis();

        final int width = bitmap.getWidth();
        final float centreLine = (float) bitmap.getHeight() / 2f;
        final float[] normalizedAmplitudes = new float[width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < centreLine; y++) {
                if (isWaveform(bitmap, x, y)) {
                    normalizedAmplitudes[x] = (centreLine - y) / centreLine;
                    break; // next sample in x.
                }
            }
        }
        Timber.d("Waveform extraction took: " + (System.currentTimeMillis() - debugStartTime)
                 + " ms");
        return normalizedAmplitudes;
    }

    protected abstract boolean isWaveform(@NonNull final Bitmap bitmap,
                                          final int x,
                                          final int y);

}
