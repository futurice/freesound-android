package com.futurice.freesound.feature.common;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

public abstract class WaveformExtractor {

    private static final String TAG = WaveformExtractor.class.getSimpleName();

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
        Log.d(TAG,
              "Waveform extraction took: " + (System.currentTimeMillis() - debugStartTime) + " ms");
        return normalizedAmplitudes;
    }

    protected abstract boolean isWaveform(@NonNull final Bitmap bitmap,
                                          final int x,
                                          final int y);

}
