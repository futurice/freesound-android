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

import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.mvi.*
import com.futurice.freesound.network.api.model.User
import timber.log.Timber

internal class HomeFragmentViewModel(private val tag: String, store: Store<HomeUiModel, Action, Result>)
    : BaseViewModel<UiEvent, Action, Result, HomeUiModel>("HomeFragmentViewModel",
        initialEvent = UiEvent.InitialEvent,
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
            UiEvent.InitialEvent -> Action.Initial
            UiEvent.RefreshRequested -> Action.RefreshContent
            UiEvent.ErrorIndicatorDismissed -> Action.ClearError
        }.also { Timber.d("From UiEvent: $uiEvent, Action is: $it") }
    }

}

sealed class Result(val log: String) {
    object NoChange : Result("No-op change")
    object ErrorCleared : Result("Error dismissed change")
    data class Refreshed(val refresh: Operation) : Result("User Fetch state change: $refresh")
    data class UserUpdated(val userFetch: Fetch<User>) : Result("User state change: $userFetch")
}

sealed class Action(val log: String) {
    object Initial : Action("Initial action")
    object ClearError : Action("Error cleared action")
    object RefreshContent : Action("Content refresh action")
}
