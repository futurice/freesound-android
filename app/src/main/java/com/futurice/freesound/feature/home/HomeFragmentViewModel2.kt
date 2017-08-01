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
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

internal class HomeFragmentViewModel2(private val userDataModel: UserDataModel,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<Fragment.UiEvent, Fragment.UiModel>(schedulers) {

    private val uiEvents: PublishProcessor<Fragment.UiEvent> = PublishProcessor.create()
    private val uiModel: Observable<Fragment.UiModel>
    private val disposable: Disposable

    val INITIAL_UI_STATE: Fragment.UiModel get() = Fragment.UiModel(null, false, null)

    fun transform(): ObservableTransformer<Fragment.UiEvent, Fragment.Change> {

        val contentRefreshRequested: ObservableTransformer<Fragment.UiEvent.ContentRefreshRequested, UserAction.Fetch>
                = ObservableTransformer {
            it.map { UserAction.Fetch }
        }

        (1..10).map {  }

        uiEvents

        val fetchUser:
                ObservableTransformer<UserAction.Fetch, FetchUserResult> =
                ObservableTransformer {
                    userDataModel.homeUser
                            .map { FetchUserResult.UserDataEvent(it) }
                            .map { it as FetchUserResult }
                            .toObservable()
                            .startWith(FetchUserResult.UserFetchInProgressEvent)
                            .onErrorResumeNext { e: Throwable ->
                                Observable.just(FetchUserResult.UserFetchFailureEvent(e))
                            }
                }


        val dismissErrorIndicator:
                ObservableTransformer<Fragment.UiEvent.ErrorIndicatorDismissed, FetchUserResult> =
                ObservableTransformer {

                }
    }

        private fun reduce(): Observable<M> {
            return results()
                    .scan(INITIAL_UI_STATE, { model: M, change -> model.reduce(change) })
                    .doOnNext { model: M -> Timber.v(" $model") }
        }

        init {
            uiModel = reduce().replayingShare()
            disposable = (uiModel
                    .subscribeOn(schedulers.computation())
                    .subscribe({ Timber.d("## $it") }, { e -> Timber.e("## $e") }))
        }

        override fun uiEvents(uiEvent: UE) {
            uiEvents.offer(uiEvent)
        }

        override fun uiModels(): Observable<M> = uiModel


        override fun onCleared() {
            super.onCleared()
            disposable.dispose()
        }

        override fun mapUiEvent(uiEvent: Fragment.UiEvent) =
                when (uiEvent) {
                    Fragment.UiEvent.ErrorIndicatorDismissed -> Observable.just(Fragment.Change.ErrorIndicatorDismissedChanged)
                    Fragment.UiEvent.ContentRefreshRequested -> Observable.just(Fragment.Change.ContentRefreshRequestedChanged)
                }

        override fun mapDataEvent(dataEvent: Fragment.DataEvent): Observable<Fragment.Change> =
                when (dataEvent) {
                    Fragment.DataEvent.UserFetchInProgressEvent -> Observable.just(Fragment.Change.UserFetchInProgressChanged)
                    is Fragment.DataEvent.UserDataEvent -> Observable.just(Fragment.Change.UserChanged(dataEvent.user))
                    is Fragment.DataEvent.UserFetchFailedEvent -> Observable.just(Fragment.Change.UserFetchErrorChanged(dataEvent.error.localizedMessage))
                }

        override fun Fragment.UiModel.reduce(change: Fragment.Change): Fragment.UiModel =
                when (change) {
                    is Fragment.Change.NoChange -> this
                    Fragment.Change.UserFetchInProgressChanged -> fromUserFetchInProgressChange()
                    is Fragment.Change.UserChanged -> fromUserChanged(change)
                    is Fragment.Change.UserFetchErrorChanged -> fromUserFetchErrorChanged(change)
                    Fragment.Change.ErrorIndicatorDismissedChanged -> fromErrorIndicatorDismissedChange()
                    Fragment.Change.ContentRefreshRequestedChanged -> from
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

        private fun Fragment.UiModel.fromErrorIndicatorDismissedChange() = copy(errorMsg = null)

    }

