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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.futurice.freesound.BuildConfig;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.app.ForApplication;

import android.content.Context;
import android.net.Uri;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class AudioModule {

    /*
     * Current app visual design means that instances are not shared between Activities.
     */

    @Binds
    @ActivityScope
    abstract AudioPlayer provideAudioPlayer(ExoPlayerAudioPlayer exoPlayerAudioPlayer);

    //
    // Internal
    //

    @Provides
    static MediaSourceFactory provideMediaSourceFactory(final DataSource.Factory dataSourceFactory,
                                                        final ExtractorsFactory extractorsFactory) {
        return uri -> new ExtractorMediaSource(Uri.parse(uri),
                                               dataSourceFactory,
                                               extractorsFactory,
                                               null, null);
    }

    @Binds
    @ActivityScope
    abstract ExoPlayer provideExoPlayer(SimpleExoPlayer simpleExoPlayer);

    @Binds
    abstract ObservableExoPlayer provideObservableExoPlayer(DefaultObservableExoPlayer exoPlayer);

    @Provides
    static DataSource.Factory provideDataSourceFactory(@ForApplication Context context) {
        return new DefaultDataSourceFactory(context,
                                            Util.getUserAgent(context, BuildConfig.APPLICATION_ID));
    }

    @Provides
    static ExtractorsFactory provideExtractorsFactory() {
        return new DefaultExtractorsFactory();
    }

    @Provides
    static TrackSelection.Factory provideTrackSelectionFactory() {
        return new FixedTrackSelection.Factory();
    }

    @Provides
    static TrackSelector provideTrackSelector(TrackSelection.Factory trackSelectionFactory) {
        return new DefaultTrackSelector(trackSelectionFactory);
    }

    @Provides
    static LoadControl provideLoadControl() {
        return new DefaultLoadControl();
    }

    @Provides
    static SimpleExoPlayer provideSimpleExoPlayer(@ForApplication Context context,
                                                  TrackSelector trackSelector,
                                                  LoadControl loadControl) {
        return ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
    }

}
