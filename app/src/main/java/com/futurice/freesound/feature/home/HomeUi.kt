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
import com.futurice.freesound.network.api.model.User


// Event from the UI to VM

sealed class UiEvent(val log: String) {
    object Initial : UiEvent("Initial")
    object ErrorIndicatorDismissed : UiEvent("ErrorIndicatorDismissed")
    object RefreshRequested : UiEvent("RefreshRequested")
}

// Events from VM to UI

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val isRefreshing: Boolean,
                       val errorMsg: String?)

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

sealed class Result(val log: String) {
    object NoChange : Result("No-op change")
    object ErrorCleared : Result("Error cleared change")
    data class Refreshed(val refresh: Operation) : Result("Content refreshed: $refresh")
    data class UserUpdated(val userFetch: Fetch<User>) : Result("User updated: $userFetch")
}

sealed class Action(val log: String) {
    object Initial : Action("Initial action")
    object ClearError : Action("Error cleared action")
    object RefreshContent : Action("Content refresh action")
}
