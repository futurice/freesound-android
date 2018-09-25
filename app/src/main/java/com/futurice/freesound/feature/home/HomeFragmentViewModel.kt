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

package com.futurice.freesound.feature.home

import com.futurice.freesound.mvi.*
import timber.log.Timber

internal class HomeFragmentViewModel(tag: String = "HomeFragmentViewModel",
                                     initialEvent: UiEvent = UiEvent.Initial,
                                     store: Store<HomeUiModel, Action, Result>)
    : BaseViewModel<UiEvent, Action, Result, HomeUiModel>(tag = tag,
        initialEvent = initialEvent,
        store = store) {

    companion object {
        val INITIAL_UI_STATE: HomeUiModel
            get() = HomeUiModel(
                    user = null,
                    isLoading = false,
                    isRefreshing = false,
                    errorMsg = null)
    }

    override fun eventToAction(uiEvent: UiEvent): Action {
        return when (uiEvent) {
            UiEvent.Initial -> Action.Initial
            UiEvent.RefreshRequested -> Action.RefreshContent
            UiEvent.ErrorIndicatorDismissed -> Action.ClearError
        }.also { Timber.d("From UiEvent: $uiEvent, Action is: $it") }
    }

}
