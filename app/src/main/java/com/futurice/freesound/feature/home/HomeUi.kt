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

//interface Activity {
//
//    sealed class UiEvent(val log: String) {
//        object NoOp : UiEvent("No-op")
//        object OpenSearchEvent : UiEvent("Open Search")
//    }
//
//}


// Event from the UI to VM

sealed class UiEvent(val log: String) {
    object InitialEvent : UiEvent("InitialEvent")
    object ErrorIndicatorDismissed : UiEvent("ErrorIndicatorDismissed")
    object RefreshRequested : UiEvent("RefreshRequested")
}


// Events from VM to UI

//sealed class UiModel {
//    class Data<T>(value: T) : UiModel()
//    class Error(val throwable: Throwable, val msg: String) : UiModel()
//}

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val errorMsg: String?)

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)
