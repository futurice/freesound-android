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

package com.futurice.freesound.feature.home.user

import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.mvi.*
import com.futurice.freesound.network.api.model.User

val LOG_TAG = "HomeUserUi"

val INITIAL_UI_EVENT = HomeUiEvent.Initial

val INITIAL_UI_STATE: HomeUiModel
    get() = HomeUiModel(
            user = null,
            isLoading = false,
            isRefreshing = false,
            errorMsg = null)

sealed class HomeUiEvent : Event {
    object Initial : HomeUiEvent()
    object ErrorIndicatorDismissed : HomeUiEvent()
    object RefreshRequested : HomeUiEvent()
}

val homeUserUiEventMapper: EventMapper<HomeUiEvent, HomeUiAction> =
        { uiEvent: HomeUiEvent ->
            when (uiEvent) {
                HomeUiEvent.Initial -> HomeUiAction.Initial
                HomeUiEvent.RefreshRequested -> HomeUiAction.RefreshContent
                HomeUiEvent.ErrorIndicatorDismissed -> HomeUiAction.ClearError
            }
        }


sealed class HomeUiResult : Result {
    object NoChange : HomeUiResult()
    object ErrorCleared : HomeUiResult()
    data class Refreshed(val refresh: Operation) : HomeUiResult()
    data class UserUpdated(val userFetch: Fetch<User>) : HomeUiResult()
}

sealed class HomeUiAction : Action {
    object Initial : HomeUiAction()
    object ClearError : HomeUiAction()
    object RefreshContent : HomeUiAction()
}

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val isRefreshing: Boolean,
                       val errorMsg: String?) : State

