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

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.mvi.BaseViewModel
import com.futurice.freesound.network.api.model.User
import io.reactivex.Observable

internal class HomeFragmentViewModel2(dataEvents: Observable<Fragment.DataEvent>,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<Fragment.UiEvent, Fragment.DataEvent, Fragment.UiModel, Fragment.Change>(dataEvents, schedulers) {

    override val INITIAL_UI_STATE: Fragment.UiModel get() = Fragment.UiModel(null, true, null)

    override fun fromUiEvent(uiEvent: Fragment.UiEvent): Fragment.Change =
            when (uiEvent) {
                is Fragment.UiEvent.NoOp -> Fragment.Change.NoChange
            }

    override fun fromDataEvent(dataEvent: Fragment.DataEvent): Fragment.Change =
            when (dataEvent) {
                is Fragment.DataEvent.UserDataEvent -> Fragment.Change.UserChanged(dataEvent.user)
            }

    override fun Fragment.UiModel.reduce(change: Fragment.Change): Fragment.UiModel =
            when (change) {
                is Fragment.Change.NoChange -> this
                is Fragment.Change.UserChanged -> fromUserChanged(change.user)
            }

    private fun Fragment.UiModel.fromUserChanged(user: User): Fragment.UiModel {
        return this.copy(user = Fragment.UserUiModel(user.username(),
                about = user.about(),
                avatarUrl = user.avatar().large()),
                isLoading = false,
                errorMsg = null)
    }

}
