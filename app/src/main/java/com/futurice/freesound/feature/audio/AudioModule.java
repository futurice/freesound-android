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

import android.content.Context;
import android.net.Uri;

import com.futurice.freesound.BuildConfig;
import com.futurice.freesound.inject.activity.ActivityScope;
import com.futurice.freesound.inject.app.ForApplication;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;

@Module
public class AudioModule {

    /**
     * Can't use @Binds because of this: https://github.com/google/dagger/issues/348
     */

    /*
     * Current app visual design means that instances are not shared between Activities.
     */
    @Provides
    @ActivityScope
    AudioPlayer provideAudioPlayer(ExoPlayerAudioPlayer exoPlayerAudioPlayer) {
        return exoPlayerAudioPlayer;
    }

    //
    // Internal
    //

    @Provides
    static MediaSourceFactory provideMediaSourceFactory(final ExtractorMediaSource.Factory extractorMediaSourceFactory) {
        return uri -> extractorMediaSourceFactory.createMediaSource(Uri.parse(uri));
    }

    @Provides
    Observable<ExoPlayerState> provideExoPlayerStateObservable(
            ExoPlayerStateObservable observable) {
        return observable;
    }

    @Provides
    Observable<Long> provideExoPlayerProgressObservable(
            ExoPlayerProgressObservable observable) {
        return observable;
    }

    @Provides
    @ActivityScope
    ExoPlayer provideExoPlayer(SimpleExoPlayer simpleExoPlayer) {
        return simpleExoPlayer;
    }

    @Provides
    ObservableExoPlayer provideObservableExoPlayer(DefaultObservableExoPlayer exoPlayer) {
        return exoPlayer;
    }

    @Provides
    static DataSource.Factory provideDataSourceFactory(@ForApplication Context context) {
        return new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, BuildConfig.APPLICATION_ID));
    }

    @Provides
    static ExtractorMediaSource.Factory provideExtractorMediaSourceFactory(final DataSource.Factory dataSourceFactory) {
        return new ExtractorMediaSource.Factory(dataSourceFactory);
    }

    @Provides
    static TrackSelection.Factory provideTrackSelectionFactory() {
        return new FixedTrackSelection.Factory();
    }

    @Provides
    static RenderersFactory provideRenderersFactory(@ForApplication Context context) {
        return new DefaultRenderersFactory(context);
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
                                                  RenderersFactory renderersFactory,
                                                  TrackSelector trackSelector,
                                                  LoadControl loadControl) {
        return ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl);
    }

}
