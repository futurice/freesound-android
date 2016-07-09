package com.futurice.freesound.feature.common.view;

import com.futurice.freesound.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class WaveformView extends View {

    private static final String TAG = WaveformView.class.getSimpleName();

    private static final boolean VERBOSE_LOGGING = true;

    private static final float DEFAULT_COLUMN_WIDTH_DP = 12;
    private static final int DEFAULT_COLUMN_GAP_DP = 8;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.LTGRAY;
    private static final int DEFAULT_WAVEFORM_COLOR = Color.BLACK;

    private final Rect rect = new Rect();
    private final Paint backgroundPaint;
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

        backgroundPaint = createBackgroundPaint(DEFAULT_BACKGROUND_COLOR);

    }

    public void setWaveform(float[] waveform) {
        this.waveform = waveform;
        setDrawingCacheEnabled(false);
        invalidate();
    }

    public void clear() {
        this.waveform = null;
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        final long start = System.currentTimeMillis();

        // Draw background
        canvas.drawPaint(backgroundPaint);

        // Don't draw if we haven't got anything to draw!
        if (waveform == null || waveform.length == 0) {
            return;
        }

     //   logV(TAG, "onDraw() - data width: " + waveform.length);
     //   logV(TAG, "onDraw() - canvas width: " + canvas.getWidth());
      //  logV(TAG, "onDraw() - column width & padding width: " +
      //            columnWidthPx + "," + columnGapPx);

        final float drawableWidth = canvas.getWidth() - columnGapPx;
     //   logV(TAG, "onDraw() - usableWidth: " + drawableWidth);

        // The number of whole columns that fit in the drawable width with the desired column spacing
        final int columnCount = (int) (drawableWidth / (columnGapPx));
     //   logV(TAG, "onDraw() - columns: " + columnCount);

        // The remainder, we want to shift the columns to the centre of the available width.
        final float remainder = drawableWidth % columnCount;
    //    logV(TAG, "onDraw() - remainder: " + remainder);

        // The number of datapoints that contribute to a column
        final int datapoints = waveform.length / columnCount;
     //   logV(TAG, "onDraw() - datapoint per column: " + datapoints);

        // Max height to be used by the waveform
        final int heightScalingFactor = canvas.getHeight() / 2;
        final int centreLine = canvas.getHeight() / 2;

        // Incrementing column borders
        int left = (int) (columnGapPx + remainder) / 2; // initial margin
        int right = left + columnWidthPx;

        final long iterationStart = System.currentTimeMillis();

        for (int currentColumn = 0; currentColumn < columnCount; currentColumn++) {
        //    logV(TAG, "onDraw() - drawing column: " + currentColumn);
         //   logV(TAG, "onDraw() - waveform value: " + waveform[currentColumn * datapoints]);
            final int columnLength = (int) (waveform[currentColumn * datapoints]
                                            * heightScalingFactor);
            final int top = centreLine - (columnLength / 2);
            final int bottom = top + columnLength;

        //    logV(TAG, "onDraw() - left: " + left + " right: " + right + " top: " + top + " bottom: "
        //              + bottom);

            rect.set(left, top, right, bottom);
            canvas.drawRect(rect, waveformPaint);

            // Increment for next column
            left = right + columnGapPx;
            right = left + columnWidthPx;
        }
        Log.d(TAG, "onDraw() iteration took: " + (System.currentTimeMillis() - iterationStart) + "ms");



        Log.d(TAG, "onDraw() took: " + (System.currentTimeMillis() - start) + "ms");
    }

    @NonNull
    private static Paint createWaveformPaint(@ColorInt final int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        return paint;
    }

    @NonNull
    private static Paint createBackgroundPaint(@ColorInt final int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        return paint;
    }

    private static void logV(String tag, String msg) {
        if (VERBOSE_LOGGING) {
            Log.v(tag, msg);
        }
    }

}
