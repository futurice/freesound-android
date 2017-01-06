/*
 * Copyright 2017 Futurice GmbH
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

import com.futurice.freesound.R;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaybackWaveformView extends FrameLayout implements WaveformRender {

    @BindView(R.id.waveformView_soundItem)
    WaveformView waveformView;

    @BindView(R.id.textView_soundDuration)
    TextView durationTextView;

    public PlaybackWaveformView(final Context context) {
        super(context);
        init();
    }

    public PlaybackWaveformView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlaybackWaveformView(final Context context, final AttributeSet attrs,
                                final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PlaybackWaveformView(final Context context, final AttributeSet attrs,
                                final int defStyleAttr,
                                final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_playbackwaveform, this);
        ButterKnife.bind(this);
    }

    @Override
    public void setWaveform(@NonNull final float[] waveform) {
        waveformView.setWaveform(waveform);
    }

    @Override
    public void clear() {
        waveformView.clear();
        durationTextView.setText("");
    }

    public void setMetadata(final int duration) {
        durationTextView.setText(DateUtils.formatElapsedTime(duration));
    }
}
