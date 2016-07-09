package com.futurice.freesound.feature.common;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;

public class BlackBackgroundWaveformExtractor extends WaveformExtractor {

    @Override
    public boolean isWaveform(@NonNull final Bitmap bitmap, final int x, final int y) {
        return bitmap.getPixel(x, y) != Color.BLACK;
    }
}
