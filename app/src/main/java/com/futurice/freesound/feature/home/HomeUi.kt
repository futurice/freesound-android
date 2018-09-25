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
import com.futurice.freesound.mvi.Action
import com.futurice.freesound.mvi.Event
import com.futurice.freesound.mvi.Result
import com.futurice.freesound.mvi.State
import com.futurice.freesound.network.api.model.User

sealed class HomeUiEvent(val log: String) : Event {
    object Initial : HomeUiEvent("Initial")
    object ErrorIndicatorDismissed : HomeUiEvent("ErrorIndicatorDismissed")
    object RefreshRequested : HomeUiEvent("RefreshRequested")
}

sealed class HomeUiResult(val log: String) : Result {
    object NoChange : HomeUiResult("No-op change")
    object ErrorCleared : HomeUiResult("Error cleared change")
    data class Refreshed(val refresh: Operation) : HomeUiResult("Content refreshed: $refresh")
    data class UserUpdated(val userFetch: Fetch<User>) : HomeUiResult("User updated: $userFetch")
}

sealed class HomeUiAction(val log: String) : Action {
    object Initial : HomeUiAction("Initial action")
    object ClearError : HomeUiAction("Error cleared action")
    object RefreshContent : HomeUiAction("Content refresh action")
}

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val isRefreshing: Boolean,
                       val errorMsg: String?) : State

