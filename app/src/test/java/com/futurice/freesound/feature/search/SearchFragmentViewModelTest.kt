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
import com.futurice.freesound.feature.common.DisplayableItem
import com.futurice.freesound.feature.common.Navigator
import com.futurice.freesound.feature.search.SearchResultListItems.SOUND
import com.futurice.freesound.network.api.model.Sound
import com.futurice.freesound.test.assertion.rx.RxJava2OptionAssertions
import com.futurice.freesound.test.data.TestData.Companion.sounds
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import polanski.option.Option

class SearchFragmentViewModelTest {
    @Mock
    private lateinit var searchDataModel: SearchDataModel
    @Mock
    private lateinit var navigator: Navigator
    @Mock
    private lateinit var audioPlayer: AudioPlayer

    private lateinit var viewModel: SearchFragmentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = SearchFragmentViewModel(searchDataModel,
                navigator,
                audioPlayer,
                TrampolineSchedulerProvider())
    }

    @Test
    fun `sounds emitsNone whenSearchResultsIsNone`() {
        // given
        Arrangement().enqueueSearchResults(Option.none())

        // when, then
        viewModel.soundsOnceAndStream
                .test()
                .assertValue(RxJava2OptionAssertions.isNone())
    }

    @Test
    fun `sounds emitsSearchResultsWrappedInDisplayableItems`() {
        // given
        val sounds = sounds(10)
        Arrangement().enqueueSearchResults(Option.ofObj(sounds))

        // when, then
        viewModel.soundsOnceAndStream
                .test()
                .assertValue(RxJava2OptionAssertions.hasOptionValue(sounds.expectedDisplayableItems()))
    }

    @Test
    fun `stopsAudioPlayback byDefault`() {
        // given
        Arrangement()
        // when
        viewModel.soundsOnceAndStream.test()
        // then
        verify(audioPlayer).stopPlayback()
    }

    @Test
    fun `stopsAudioPlayback whenSearchResultChange`() {
        // given
        val arrangement = Arrangement()
        viewModel.soundsOnceAndStream.test()
        reset(audioPlayer) // is invoked by default, so reset the mock invocation count.

        // when
        arrangement.enqueueSearchResults(Option.ofObj(sounds(10)))

        // then
        verify(audioPlayer).stopPlayback()
    }

    @Test
    fun `stopPlayback stopsAudioPlayback`() {
        // given, when
        viewModel.stopPlayback()
        // then
        verify(audioPlayer).stopPlayback()
    }

    private inner class Arrangement {

        private val mockedSearchResultsStream = BehaviorSubject.createDefault<SearchState>(SearchState.Cleared)

        fun withSuccessfulSearchResultStream(): Arrangement {
            `when`(searchDataModel.searchStateOnceAndStream).thenReturn(mockedSearchResultsStream)
            return this
        }

        fun enqueueSearchResults(sounds: Option<List<Sound>>): Arrangement {
            sounds.ifSome { soundList: List<Sound> -> mockedSearchResultsStream.onNext(SearchState.Success(soundList)) }
                    .ifNone { mockedSearchResultsStream.onNext(SearchState.Cleared) }
            return this
        }

        init {
            withSuccessfulSearchResultStream()
        }
    }

    private fun List<Sound>.expectedDisplayableItems(): List<DisplayableItem<Sound>> {
        return Observable.fromIterable(this)
                .map { DisplayableItem(it, SOUND) }
                .toList()
                .blockingGet()
    }
}