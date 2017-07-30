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
import io.reactivex.Observable

internal class HomeFragmentViewModel2(dataEvents: Observable<Fragment.DataEvent>,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<Fragment.UiEvent, Fragment.DataEvent, Fragment.UiModel, Fragment.Change>(dataEvents, schedulers) {

    override val INITIAL_UI_STATE: Fragment.UiModel get() = Fragment.UiModel(null, false, null)

    override fun mapUiEvent(uiEvent: Fragment.UiEvent): Fragment.Change = Fragment.Change.NoChange

    override fun mapDataEvent(dataEvent: Fragment.DataEvent): Fragment.Change =
            when (dataEvent) {
                Fragment.DataEvent.UserFetchInProgressEvent -> Fragment.Change.UserFetchInProgressChanged
                is Fragment.DataEvent.UserDataEvent -> Fragment.Change.UserChanged(dataEvent.user)
                is Fragment.DataEvent.UserFetchFailedEvent -> Fragment.Change.UserFetchErrorChanged(dataEvent.error.localizedMessage)
            }

    override fun Fragment.UiModel.reduce(change: Fragment.Change): Fragment.UiModel =
            when (change) {
                is Fragment.Change.NoChange -> this
                Fragment.Change.UserFetchInProgressChanged -> fromUserFetchInProgressChange()
                is Fragment.Change.UserChanged -> fromUserChanged(change)
                is Fragment.Change.UserFetchErrorChanged -> fromUserFetchErrorChanged(change)
            }

    private fun Fragment.UiModel.fromUserChanged(change: Fragment.Change.UserChanged): Fragment.UiModel
            = copy(user = Fragment.UserUiModel(change.user.username(),
            about = change.user.about(),
            avatarUrl = change.user.avatar().large()),
            isLoading = false,
            errorMsg = null)

    private fun Fragment.UiModel.fromUserFetchErrorChanged(change: Fragment.Change.UserFetchErrorChanged)
            = copy(isLoading = false, errorMsg = change.errorMsg)

    private fun Fragment.UiModel.fromUserFetchInProgressChange() = copy(isLoading = true)
}
