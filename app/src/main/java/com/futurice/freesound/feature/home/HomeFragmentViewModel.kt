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

internal class HomeFragmentViewModel(tag: String = LOG_TAG,
                                     logger: Logger,
                                     initialEvent: HomeUiEvent = HomeUiEvent.Initial,
                                     store: Store<HomeUiAction, HomeUiResult, HomeUiModel>)
    : BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>(tag = tag,
        logger = logger,
        initialEvent = initialEvent,
        store = store) {

    companion object {
        val LOG_TAG = "HomeFragmentViewModel"
        val INITIAL_UI_STATE: HomeUiModel
            get() = HomeUiModel(
                    user = null,
                    isLoading = false,
                    isRefreshing = false,
                    errorMsg = null)
    }

    override fun eventToAction(uiEvent: HomeUiEvent): HomeUiAction {
        return when (uiEvent) {
            HomeUiEvent.Initial -> HomeUiAction.Initial
            HomeUiEvent.RefreshRequested -> HomeUiAction.RefreshContent
            HomeUiEvent.ErrorIndicatorDismissed -> HomeUiAction.ClearError
        }
    }

}
