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

package com.futurice.freesound.feature.audio;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.subjects.PublishSubject;
import polanski.option.Option;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExoPlayerAudioPlayerTest {

    @Mock
    private ExoPlayer exoPlayer;

    @Mock
    private ExoPlayerStateObservableFactory exoPlayerStateObservableFactory;

    @Mock
    private MediaSourceFactory mediaSourceFactory;

    private ExoPlayerAudioPlayer exoPlayerAudioPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        exoPlayerAudioPlayer = new ExoPlayerAudioPlayer(exoPlayer,
                                                        exoPlayerStateObservableFactory,
                                                        mediaSourceFactory);
    }

    @Test
    public void stop_stopsExoPlayer() {
        exoPlayerAudioPlayer.stop();

        verify(exoPlayer).stop();
    }

    @Test
    public void stop_clearsCurrentUrl() {
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        Act act = arrangeBuilder.act();
        act.playingUrl("someUrl");

        exoPlayerAudioPlayer.stop();
        arrangeBuilder
                .withExoPlayerStateStreamEvent(ExoPlayerState.create(true, ExoPlayer.STATE_IDLE));

        exoPlayerAudioPlayer.getPlayerStateStream()
                            .test()
                            .assertValue(v -> v.id().equals(Option.NONE));
    }

    @Test
    public void getPlayerStateStream_initiallyHasNoPlaybackUrl() {
        new ArrangeBuilder()
                .withIdleExoPlayer();

        exoPlayerAudioPlayer.getPlayerStateStream()
                            .test()
                            .assertValue(v -> v.id().equals(Option.NONE));
    }

    @Test
    public void toggle_setsPlaybackUrl() {
        String url = "someUrl";
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withIdleExoPlayer();

        exoPlayerAudioPlayer.toggle(url);
        arrangeBuilder
                .withExoPlayerStateStreamPlayingEvent();

        exoPlayerAudioPlayer.getPlayerStateStream()
                            .test()
                            .assertValue(v -> v.id().equals(Option.ofObj(url)));
    }

    @Test
    public void toggle_pausesSource_whenPlaying() {
        new ArrangeBuilder()
                .withPlayingExoPlayer();

        exoPlayerAudioPlayer.toggle("url");

        verify(exoPlayer).setPlayWhenReady(false);
    }

    @Test
    public void toggle_unpausesSource_whenPause() {
        new ArrangeBuilder()
                .withPausedExoPlayer();

        exoPlayerAudioPlayer.toggle("url");

        verify(exoPlayer).setPlayWhenReady(true);
        verify(mediaSourceFactory, never()).create(anyString());
        verify(exoPlayer, never()).prepare(any());
    }

    @Test
    public void toggle_playsSource_whenIdle() {
        String url = "url";
        new ArrangeBuilder()
                .withIdleExoPlayer()
                .withMediaSource();

        exoPlayerAudioPlayer.toggle(url);

        verify(mediaSourceFactory).create(url);
        verify(exoPlayer).prepare(any(MediaSource.class));
        verify(exoPlayer).setPlayWhenReady(eq(true));
    }

    @Test
    public void toggle_doesNotClearPlaybackUrl_whenPausing() {
        String url = "url";
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withPlayingExoPlayer()
                .withMediaSource();

        exoPlayerAudioPlayer.toggle(url);
        arrangeBuilder.withExoPlayerStateStreamPausedEvent();

        exoPlayerAudioPlayer.getPlayerStateStream()
                            .test()
                            .assertValue(v -> v.id().isSome());
    }

    @Test
    public void release_releasesExoPlayer() {
        exoPlayerAudioPlayer.release();

        verify(exoPlayer).release();
    }

    private class ArrangeBuilder {

        private final PublishSubject<ExoPlayerState> exoPlayerStateStream = PublishSubject.create();

        ArrangeBuilder() {
            when(exoPlayerStateObservableFactory.create(any())).thenReturn(exoPlayerStateStream);
        }

        ArrangeBuilder withMediaSource() {
            when(mediaSourceFactory.create(anyString())).thenReturn(mock(MediaSource.class));
            return this;
        }

        ArrangeBuilder withIdleExoPlayer() {
            return withCurrentExoPlayerState(true, ExoPlayer.STATE_IDLE);
        }

        ArrangeBuilder withPlayingExoPlayer() {
            return withCurrentExoPlayerState(true, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withPausedExoPlayer() {
            return withCurrentExoPlayerState(false, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withExoPlayerStateStreamPlayingEvent() {
            exoPlayerStateStream.onNext(ExoPlayerState.create(true, ExoPlayer.STATE_READY));
            return this;
        }

        ArrangeBuilder withExoPlayerStateStreamPausedEvent() {
            exoPlayerStateStream.onNext(ExoPlayerState.create(false, ExoPlayer.STATE_READY));
            return this;
        }

        ArrangeBuilder withCurrentExoPlayerState(boolean playWhenReady, int playbackState) {
            when(exoPlayer.getPlayWhenReady()).thenReturn(playWhenReady);
            when(exoPlayer.getPlaybackState()).thenReturn(playbackState);
            return this;
        }

        ArrangeBuilder withExoPlayerStateStreamEvent(ExoPlayerState exoPlayerState) {
            exoPlayerStateStream.onNext(exoPlayerState);
            return this;
        }

        Act act() {
            return new Act();
        }
    }

    private class Act {

        Act playingUrl(String url) {
            exoPlayerAudioPlayer.toggle(url);
            return this;
        }

    }

}
