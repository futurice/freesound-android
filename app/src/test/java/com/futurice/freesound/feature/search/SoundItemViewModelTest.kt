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

package com.futurice.freesound.feature.search

import com.futurice.freesound.feature.audio.AudioPlayer
import com.futurice.freesound.feature.audio.PlaybackSource
import com.futurice.freesound.feature.audio.PlayerState
import com.futurice.freesound.feature.audio.State
import com.futurice.freesound.feature.common.Navigator
import com.futurice.freesound.network.api.FreeSoundApiService
import com.futurice.freesound.network.api.model.Sound
import com.futurice.freesound.network.api.model.User
import com.futurice.freesound.test.data.TestData

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import polanski.option.Option

import com.futurice.freesound.feature.audio.from
import com.futurice.freesound.network.api.model.Image
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class SoundItemViewModelTest {

    @Mock
    private lateinit var navigator: Navigator

    @Mock
    private lateinit var audioPlayer: AudioPlayer

    @Mock
    private lateinit var freeSoundApiService: FreeSoundApiService

    @Mock
    private lateinit var tabController: TabController

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun userAvatar_isUserMediumAvatar() {
        val username = "username"
        val avatar_m = "avatar_m"
        val sound = TEST_SOUND.copy(username = username)
        val user = TestData.user()
                .copy(avatar = TestData.avatar().copy(medium = avatar_m))

        ArrangeBuilder().withUserResponse(username, user)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.userAvatar()
                .test()
                .assertValue(avatar_m)
    }

    @Test
    fun username_isUsersUsername() {
        val username = "username"
        val sound = TEST_SOUND.copy(username = username)
        val user = TestData.user().copy(username = username)
        ArrangeBuilder().withUserResponse(username, user)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.username()
                .test()
                .assertValue(username)
    }

    @Test
    fun created_isSoundsCreatedDate() {
        val createdDate = Date(1000L)
        val sound = TEST_SOUND.copy(created = createdDate)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.createdDate()
                .test()
                .assertValue(DateFormat.getDateInstance().format(createdDate))
    }

    @Test
    fun thumbnailImageUrl_emitsSoundMediumWaveformUrl() {
        val soundItemViewModel = SoundItemViewModel(TEST_SOUND,
                navigator,
                audioPlayer,
                freeSoundApiService,
                tabController)

        soundItemViewModel.thumbnailImageUrl()
                .test()
                .assertValue(TEST_SOUND.images.medSizeWaveformUrl)
    }

    @Test
    fun name_emitsSoundName() {
        val soundItemViewModel = SoundItemViewModel(TEST_SOUND,
                navigator,
                audioPlayer,
                freeSoundApiService,
                tabController)

        soundItemViewModel.name()
                .test()
                .assertValue(TEST_SOUND.name)
    }

    @Test
    fun description_emitsSoundDescription() {
        val soundItemViewModel = SoundItemViewModel(TEST_SOUND,
                navigator,
                audioPlayer,
                freeSoundApiService,
                tabController)

        soundItemViewModel.description()
                .test()
                .assertValue(TEST_SOUND.description)
    }

    @Test
    fun duration_roundsUp_fromPoint4() {
        val sound = TEST_SOUND.copy(duration = 0.4f)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.duration()
                .test()
                .assertValue(1)
    }

    @Test
    fun duration_roundsUp_fromPoint5() {
        val sound = TEST_SOUND.copy(duration = 2.6f)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.duration()
                .test()
                .assertValue(3)
    }

    @Test
    fun duration_roundingUp_doesNotAffectWholeValues() {
        val sound = TEST_SOUND.copy(duration = 1f)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.duration()
                .test()
                .assertValue(1)
    }

    @Test
    fun duration_hasMinimumOf1Second() {
        val sound = TEST_SOUND.copy(duration = 0f)

        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.duration()
                .test()
                .assertValue(1)
    }

    @Test
    fun progressPercentage_isAudioPlayerProgressPercentage_whenSoundActiveInPlayer_andPositionNonZero() {
        val positionMs = TimeUnit.SECONDS.toMillis(10)
        val durationSec = 200f
        val expectedPercentage = 5
        val sound = TEST_SOUND.copy(id = 1L, url = "url", duration = durationSec)
        ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.ofObj(expectedPercentage))
    }

    @Test
    fun progressPercentage_expectedPercentage_0() {
        val positionMs: Long = 0
        val durationSec = 200f
        val expectedPercentage = 0
        val sound = TEST_SOUND.copy(id = 1L, url = "url", duration = durationSec)
        ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.ofObj(expectedPercentage))
    }

    @Test
    fun progressPercentage_expectedPercentage_99Point9() {
        val positionMs = 999L
        val durationSec = 1f
        val expectedPercentage = 99
        val sound = TEST_SOUND.copy(id = 1L, url = "url", duration = durationSec)
        ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.ofObj(expectedPercentage))
    }

    @Test
    fun progressPercentage_expectedPercentage_100() {
        val positionMs: Long = 1000
        val durationSec = 1f
        val expectedPercentage = 100
        val sound = TEST_SOUND.copy(id = 1L, url = "url", duration = durationSec)
        ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.ofObj(expectedPercentage))
    }

    @Test
    fun progressPercentage_LimitedTo100() {
        val positionMs: Long = 2000
        val durationSec = 1f
        val expectedPercentage = 100
        val sound = TEST_SOUND.copy(id = 1L, url = "url", duration = durationSec)
        ArrangeBuilder()
                .withPlayerStateEvent(playerStatewithSound(State.PLAYING, sound))
                .withPlayerProgressEvent(positionMs)
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.ofObj(expectedPercentage))
    }

    @Test
    fun progressPercentage_isNone_whenDifferentSoundActiveInPlayer_usingId() {
        val id1 = 1L
        val url1 = "url"
        val id2 = 2L
        val sound = TEST_SOUND.copy(id = id1, url = url1)
        ArrangeBuilder()
                .withPlayerStateEvent(PlayerState(State.PLAYING,
                        Option.ofObj(PlaybackSource(from(id2),
                                url1))))
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.none<Int>())
    }

    @Test
    fun progressPercentage_isNone_whenDifferentSoundActiveInPlayer_usingUrl() {
        val id1 = 1L
        val url1 = "url"
        val id2 = 2L
        val sound = TEST_SOUND.copy(id = id1, url = url1)
        ArrangeBuilder()
                .withPlayerStateEvent(PlayerState(State.PLAYING,
                        Option.ofObj(PlaybackSource(from(id2),
                                url1))))
        val vm = SoundItemViewModel(sound, navigator, audioPlayer,
                freeSoundApiService, tabController)

        vm.progressPercentage()
                .test()
                .assertValue(Option.none<Int>())
    }

    @Test
    fun openDetails_openSoundViaNavigator() {
        SoundItemViewModel(TEST_SOUND, navigator, audioPlayer, freeSoundApiService, tabController)
                .openDetails()

        verify(navigator).openSoundDetails(eq(TEST_SOUND))
    }

    @Test
    fun hasGeotag() {
        val vm = SoundItemViewModel(TEST_SOUND, navigator, audioPlayer,
                freeSoundApiService, tabController)

        assertThat(vm.hasGeoTag()).isTrue()
    }

    @Test
    fun hasNoGeotag() {
        val vm = SoundItemViewModel(TEST_SOUND.copy(geotag = null), navigator, audioPlayer,
                freeSoundApiService, tabController)

        assertThat(vm.hasGeoTag()).isFalse()
    }

    @Test
    fun requestTab() {
        SoundItemViewModel(TEST_SOUND, navigator, audioPlayer, freeSoundApiService, tabController)
                .requestTab()

        verify(tabController).requestTab(SoundInfo(TabType.MAP, Option.ofObj(TEST_SOUND)))
    }

    private inner class ArrangeBuilder internal constructor() {

        private val playerStateOnceAndStream = BehaviorSubject.createDefault(PlayerState(State.IDLE, Option.none<PlaybackSource>()))
        private val playerProgressOnceAndStream = BehaviorSubject
                .createDefault(0L)

        init {
            `when`(audioPlayer.playerStateOnceAndStream).thenReturn(playerStateOnceAndStream)
            `when`(audioPlayer.timePositionMsOnceAndStream)
                    .thenReturn(playerProgressOnceAndStream)
        }

        internal fun withPlayerStateEvent(playerState: PlayerState): ArrangeBuilder {
            playerStateOnceAndStream.onNext(playerState)
            return this
        }

        internal fun withPlayerProgressEvent(progress: Long): ArrangeBuilder {
            playerProgressOnceAndStream.onNext(progress)
            return this
        }

        internal fun withUserResponse(username: String, user: User): ArrangeBuilder {
            `when`(freeSoundApiService.getUser(eq(username))).thenReturn(Single.just(user))
            return this
        }
    }

    companion object {

        private val TEST_SOUND = TestData.sound(1L)

        // Helpers

        private fun playerStatewithSound(state: State,
                                         sound: Sound): PlayerState {
            return PlayerState(state,
                    Option.ofObj(
                            PlaybackSource(from(sound.id),
                                    sound.url)))
        }
    }

}
