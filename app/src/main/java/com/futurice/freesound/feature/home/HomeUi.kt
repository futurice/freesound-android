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

import com.futurice.freesound.network.api.model.User
import io.reactivex.Observable

interface Activity {
    data class UiModel(val thing: String)

    sealed class UiEvent(val log: String) {
        object NoOp : UiEvent("No-op")
        object OpenSearchEvent : UiEvent("Open Search")
    }

    fun uiEvents(): Observable<UiEvent>
}

interface Fragment {
    data class UiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

    sealed class UiEvent(val log: String) {
        object NoOp : UiEvent("No-op UIEvent")
    }

    sealed class Action(val log: String) {
        object NoOp : Action("No-op data Action")
    }

    sealed class DataEvent(val log: String) {
        class UserDataEvent(val user: User) : DataEvent("Home user: $user")
    }

    sealed class Change(val log: String) {
        object NoOp : Change("No-op Change")
        class UserModified(val user: User) : Change("Home user modified: $user")
    }

}
