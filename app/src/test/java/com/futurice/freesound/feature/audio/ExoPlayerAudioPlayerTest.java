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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import io.reactivex.subjects.BehaviorSubject;
import polanski.option.Option;

import static com.futurice.freesound.test.assertion.rx.RxJava2OptionAssertions.isNone;
import static com.futurice.freesound.test.assertion.rx.RxJava2OptionAssertions.isSome;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExoPlayerAudioPlayerTest {

    @Mock
    private ExoPlayer exoPlayer;

    @Mock
    private MediaSourceFactory mediaSourceFactory;

    @Mock
    private ObservableExoPlayer observableExoPlayer;

    private ExoPlayerAudioPlayer exoPlayerAudioPlayer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        exoPlayerAudioPlayer = new ExoPlayerAudioPlayer(exoPlayer,
                                                        observableExoPlayer,
                                                        mediaSourceFactory);
    }

    @Test
    public void stop_stopsExoPlayer() {
        exoPlayerAudioPlayer.stopPlayback();

        verify(exoPlayer).stop();
    }

    @Test
    public void stop_clearsCurrentUrl() {
        PlaybackSource playbackSource = new PlaybackSource(new Id("id"), "url");
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        arrangeBuilder.act()
                      .togglePlayback(playbackSource)
                      .init();
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.stopPlayback();
        arrangeBuilder.withIdleExoPlayer();

        exoPlayerAudioPlayer.getPlayerStateOnceAndStream()
                            .test()
                            .assertValue(isNone(PlayerState::getSource));
    }

    @Test
    public void getPlayerStateStream_initiallyHasNoPlaybackUrl() {
        new ArrangeBuilder()
                .withIdleExoPlayer();

        exoPlayerAudioPlayer.init();

        exoPlayerAudioPlayer.getPlayerStateOnceAndStream()
                            .test()
                            .assertValue(isNone(PlayerState::getSource));
    }

    @Test
    public void toggle_toPlay_playsSource() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        new ArrangeBuilder()
                .withIdleExoPlayer()
                .withMediaSource()
                .act()
                .init();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);

        verify(mediaSourceFactory).create(url);
        verify(exoPlayer).prepare(any(MediaSource.class));
        verify(exoPlayer).setPlayWhenReady(true);
    }

    @Test
    public void toggle_toPlay_setsPlaybackUrl() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        arrangeBuilder.withIdleExoPlayer()
                      .withMediaSource()
                      .act()
                      .init();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.getPlayerStateOnceAndStream()
                            .test()
                            .assertValue(v -> v.getSource().equals(Option.ofObj(playbackSource)));
    }

    @Test
    public void toggle_toPause_pausesSource() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        arrangeBuilder.withIdleExoPlayer()
                      .withMediaSource()
                      .act()
                      .init()
                      .togglePlayback(playbackSource);
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);

        verify(exoPlayer).setPlayWhenReady(false);
    }

    @Test
    public void toggle_toPause_retainsPlaybackUrl() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        arrangeBuilder.withIdleExoPlayer()
                      .withMediaSource()
                      .act()
                      .init()
                      .togglePlayback(playbackSource);
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);
        arrangeBuilder.withPausedExoPlayer();

        exoPlayerAudioPlayer.getPlayerStateOnceAndStream()
                            .test()
                            .assertValue(isSome(PlayerState::getSource));
    }

    @Test
    public void toggle_toUnpause_unpausesSource() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder();
        arrangeBuilder.withIdleExoPlayer()
                      .withMediaSource()
                      .act()
                      .init()
                      .togglePlayback(playbackSource);
        arrangeBuilder.withPlayingExoPlayer();
        arrangeBuilder.act()
                      .togglePlayback(playbackSource);
        arrangeBuilder.withPausedExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);

        InOrder inOrder = inOrder(exoPlayer, mediaSourceFactory);
        inOrder.verify(mediaSourceFactory).create(url);
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
        inOrder.verify(exoPlayer).setPlayWhenReady(false);
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
    }

    @Test
    public void toggle_doesNotClearPlaybackUrl_whenPausing() {
        String id = "id";
        String url = "url";
        PlaybackSource playbackSource = new PlaybackSource(new Id(id), url);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withIdleExoPlayer()
                .withMediaSource();
        arrangeBuilder.act()
                      .init()
                      .togglePlayback(playbackSource);
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource);
        arrangeBuilder.withPausedExoPlayer();

        exoPlayerAudioPlayer.getPlayerStateOnceAndStream()
                            .test()
                            .assertValue(isSome(PlayerState::getSource));
    }

    @Test
    public void toggle_withNewUrl_playsNewSource_whenPlaying() {
        String id1 = "id1";
        String id2 = "id2";
        String url1 = "url1";
        String url2 = "url2";
        PlaybackSource playbackSource1 = new PlaybackSource(new Id(id1), url1);
        PlaybackSource playbackSource2 = new PlaybackSource(new Id(id2), url2);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withIdleExoPlayer()
                .withMediaSource();
        arrangeBuilder.act()
                      .init()
                      .togglePlayback(playbackSource1);
        arrangeBuilder.withPlayingExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource2);

        InOrder inOrder = inOrder(exoPlayer, mediaSourceFactory);
        inOrder.verify(mediaSourceFactory).create(url1);
        inOrder.verify(exoPlayer).prepare(any(MediaSource.class));
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
        inOrder.verify(mediaSourceFactory).create(url2);
        inOrder.verify(exoPlayer).prepare(any(MediaSource.class));
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
    }

    @Test
    public void toggle_withNewUrl_playsNewSource_whenEnded() {
        String id1 = "id1";
        String id2 = "id2";
        String url1 = "url1";
        String url2 = "url2";
        PlaybackSource playbackSource1 = new PlaybackSource(new Id(id1), url1);
        PlaybackSource playbackSource2 = new PlaybackSource(new Id(id2), url2);
        ArrangeBuilder arrangeBuilder = new ArrangeBuilder()
                .withIdleExoPlayer()
                .withMediaSource();
        arrangeBuilder.act()
                      .init()
                      .togglePlayback(playbackSource1);
        arrangeBuilder.withEndedExoPlayer();

        exoPlayerAudioPlayer.togglePlayback(playbackSource2);

        InOrder inOrder = inOrder(exoPlayer, mediaSourceFactory);
        inOrder.verify(mediaSourceFactory).create(url1);
        inOrder.verify(exoPlayer).prepare(any(MediaSource.class));
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
        inOrder.verify(mediaSourceFactory).create(url2);
        inOrder.verify(exoPlayer).prepare(any(MediaSource.class));
        inOrder.verify(exoPlayer).setPlayWhenReady(true);
    }

    @Test
    public void release_releasesExoPlayer() {
        exoPlayerAudioPlayer.release();

        verify(exoPlayer).release();
    }

    private class ArrangeBuilder {

        private final BehaviorSubject<ExoPlayerState> exoPlayerStateStream = BehaviorSubject
                .create();
        private final BehaviorSubject<Long> exoPlayerProgressStream = BehaviorSubject.create();

        ArrangeBuilder() {
            when(observableExoPlayer.getExoPlayerStateOnceAndStream())
                    .thenReturn(exoPlayerStateStream);
            when(observableExoPlayer.getTimePositionMsOnceAndStream(1, TimeUnit.SECONDS))
                    .thenReturn(exoPlayerProgressStream);
        }

        ArrangeBuilder withMediaSource() {
            when(mediaSourceFactory.create(anyString())).thenReturn(mock(MediaSource.class));
            return this;
        }

        ArrangeBuilder withIdleExoPlayer() {
            return withExoPlayerStateStreamEvent(false, ExoPlayer.STATE_IDLE);
        }

        ArrangeBuilder withPlayingExoPlayer() {
            return withExoPlayerStateStreamEvent(true, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withPausedExoPlayer() {
            return withExoPlayerStateStreamEvent(false, ExoPlayer.STATE_READY);
        }

        ArrangeBuilder withEndedExoPlayer() {
            return withExoPlayerStateStreamEvent(true, ExoPlayer.STATE_ENDED);
        }

        ArrangeBuilder withExoPlayerStateStreamEvent(boolean playWhenReady, int state) {
            exoPlayerStateStream.onNext(new ExoPlayerState(playWhenReady, state));
            return this;
        }

        Act act() {
            return new Act();
        }

    }

    private class Act {

        Act init() {
            exoPlayerAudioPlayer.init();
            return this;
        }

        Act togglePlayback(PlaybackSource playbackSource) {
            exoPlayerAudioPlayer.togglePlayback(playbackSource);
            return this;
        }

    }

}
