package com.futurice.freesound.feature.common.view;

import com.futurice.freesound.feature.common.WaveformExtractor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

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
        Log.i("TARGET", "Target" + Thread.currentThread());
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
