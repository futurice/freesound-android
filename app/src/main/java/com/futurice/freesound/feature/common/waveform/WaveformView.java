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

package com.futurice.freesound.feature.common.waveform;

import com.futurice.freesound.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import timber.log.Timber;

public class WaveformView extends View implements WaveformRender {

    private static final boolean VERBOSE_LOGGING = true;

    private static final float DEFAULT_COLUMN_WIDTH_DP = 12;
    private static final int DEFAULT_COLUMN_GAP_DP = 8;
    private static final int DEFAULT_WAVEFORM_COLOR = Color.BLACK;

    private final Rect rect = new Rect();
    private final Paint waveformPaint;

    private final int columnWidthPx; // waveform column
    private final int columnGapPx; // padding between columns

    private float[] waveform;

    public WaveformView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                                                                 R.styleable.Waveform,
                                                                 0, 0);

        try {
            waveformPaint = createWaveformPaint(a.getColor(R.styleable.Waveform_waveformColor,
                                                           DEFAULT_WAVEFORM_COLOR));
            columnWidthPx = a.getDimensionPixelSize(R.styleable.Waveform_columnWidth,
                                                    (int) TypedValue.applyDimension(
                                                            TypedValue.COMPLEX_UNIT_DIP,
                                                            DEFAULT_COLUMN_WIDTH_DP,
                                                            getResources().getDisplayMetrics()));

            columnGapPx = a.getDimensionPixelSize(R.styleable.Waveform_columnGap,
                                                  (int) TypedValue.applyDimension(
                                                          TypedValue.COMPLEX_UNIT_DIP,
                                                          DEFAULT_COLUMN_GAP_DP,
                                                          getResources().getDisplayMetrics()));
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setWaveform(@NonNull float[] waveform) {
        this.waveform = waveform.clone();
        invalidate();
    }

    @Override
    public void clearWaveform() {
        waveform = null;
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        final long startTime = System.currentTimeMillis();

        // Don't draw if we haven't got anything to draw!
        if (waveform == null || waveform.length == 0) {
            Timber.w("Empty waveform!");
            return;
        }

        final float drawableWidth = getWidth();

        // The number of whole columns that fit in the drawable width with the desired column spacing
        final int columnCount = Math.min(waveform.length,
                                         (int) (drawableWidth / (columnWidthPx + columnGapPx)));

        // The remainder, we want to shift the columns to the centre of the available width.
        final float remainder = drawableWidth % columnCount;

        // The number of datapoints that contribute to a column
        final int datapoints = waveform.length / columnCount;

        // Max height to be used by the waveform
        final int heightScalingFactor = getHeight() / 2;
        final int centreLine = getHeight() / 2;

        // Incrementing column borders
        int left = (int) (columnGapPx + remainder) / 2; // initial margin
        int right = left + columnWidthPx;

        final long iterationStart = System.currentTimeMillis();

        for (int currentColumn = 0; currentColumn < columnCount; currentColumn++) {
            final int columnLength = (int) (waveform[currentColumn * datapoints]
                                            * heightScalingFactor);

            final int top = centreLine - (columnLength / 2);
            final int bottom = top + columnLength;

            rect.set(left, top, right, bottom);
            canvas.drawRect(rect, waveformPaint);

            // Increment for next column
            left = right + columnGapPx;
            right = left + columnWidthPx;
        }
        Timber.d("onDraw() iteration took: %d ms", System.currentTimeMillis() - iterationStart);
        Timber.d("onDraw() took: %d ms", System.currentTimeMillis() - startTime);
    }

    @NonNull
    private static Paint createWaveformPaint(@ColorInt final int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        return paint;
    }

}
