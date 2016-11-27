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

package com.futurice.freesound.feature.audio;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import com.futurice.freesound.inject.app.ForApplication;

import android.content.Context;
import android.os.Handler;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module
public final class AudioModule {

    @Provides
    static MediaSourceFactory provideMediaSourceFactory(final DataSource.Factory dataSourceFactory,
                                                        final ExtractorsFactory extractorsFactory) {
        return uri -> new ExtractorMediaSource(uri,
                                               dataSourceFactory,
                                               extractorsFactory,
                                               null,
                                               (ExtractorMediaSource.EventListener) error -> Timber
                                                       .e(error, "onLoadError"));
    }

    @Provides
    static DataSource.Factory provideDataSourceFactory(@ForApplication Context context) {
        return new DefaultDataSourceFactory(context,
                                            Util.getUserAgent(context, "yourApplicationName"),
                                            new TransferListener<DataSource>() {
                                                @Override
                                                public void onTransferStart(final DataSource source,
                                                                            final DataSpec dataSpec) {
                                                    Timber.d("onTransferStart %s %s", source,
                                                             dataSpec);
                                                }

                                                @Override
                                                public void onBytesTransferred(
                                                        final DataSource source,
                                                        final int bytesTransferred) {
                                                    Timber.d("onBytesTransferred %s %d", source,
                                                             bytesTransferred);

                                                }

                                                @Override
                                                public void onTransferEnd(final DataSource source) {
                                                    Timber.d("onTransferEnd %s", source);
                                                }
                                            });
    }

    @Provides
    static ExtractorsFactory provideExtractorsFactory() {
        return new DefaultExtractorsFactory();
    }

    @Provides
    static SimpleExoPlayer provideExoPlayer(@ForApplication Context context) {
        Handler mainHandler = new Handler();
        TrackSelection.Factory audioTrackSelectionFactory = new FixedTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(mainHandler,
                                                               audioTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        SimpleExoPlayer player = ExoPlayerFactory
                .newSimpleInstance(context, trackSelector, loadControl);

        player.setAudioDebugListener(new AudioRendererEventListener() {
            @Override
            public void onAudioEnabled(final DecoderCounters counters) {
                Timber.d("onAudioEnabled");
            }

            @Override
            public void onAudioSessionId(final int audioSessionId) {
                Timber.d("onAudioSessionId");
            }

            @Override
            public void onAudioDecoderInitialized(final String decoderName,
                                                  final long initializedTimestampMs,
                                                  final long initializationDurationMs) {
                Timber.d("onAudioDecoderInitialized");

            }

            @Override
            public void onAudioInputFormatChanged(final Format format) {
                Timber.d("onAudioInputFormatChanged");

            }

            @Override
            public void onAudioTrackUnderrun(final int bufferSize, final long bufferSizeMs,
                                             final long elapsedSinceLastFeedMs) {
                Timber.d("onAudioTrackUnderrun");

            }

            @Override
            public void onAudioDisabled(final DecoderCounters counters) {
                Timber.d("onAudioDisabled");

            }
        });
        return player;
    }

}
