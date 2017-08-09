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
import com.jakewharton.rx.replayingShare
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

    init {
        uiModel = reduce().replayingShare()
        disposable = (uiModel
                .subscribeOn(schedulers.computation())
                .subscribe({ Timber.d("## $it") }, { e -> Timber.e("## $e") }))
    }

    /**
     * The transform takes the event from the ui.
     *
     * They will be split depending on their type (when) ... and the observable transformed to
     * a change observable.
     *
     * Take UIEvents .. [[map that to an Action] .. [map that to a Result]] ... to a Change .. to a Model
     *
     * uiModel = uiEvents.compose(transform()).merge(dataEvents.compose(tranform())).reduce()
     * uiModel = uibasedchanges + databasedchanges
     *
     * Is there a way to model dataChanges as a function of ui visibility for the situations where
     * there are changes that aren't driven by obvious user events from the ui.
     *
     * perhaps that could be pulled into the parent class. Always have an event to do with "subscribing".
     */
    fun transform(): ObservableTransformer<Fragment.UiEvent, Fragment.Change> {

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

        val contentRefreshRequested: ObservableTransformer<Fragment.UiEvent.ContentRefreshRequested,
                Fragment.Change.ContentRefreshRequested>
                = ObservableTransformer {
            it.map { UserAction.Fetch }.compose(fetchUser).map { }
        }

        val dismissErrorIndicator:
                ObservableTransformer<Fragment.UiEvent.ErrorIndicatorDismissed,
                        Fragment.Change.ErrorIndicatorDismissed> =
                ObservableTransformer {
                    Observable.just(Fragment.Change.ErrorIndicatorDismissed)
                }

        return ObservableTransformer { when(it) { } }
    }

    private fun reduce(): Observable<Fragment.UiModel> {
        return uiEvents
                .compose(transform())
                .scan(INITIAL_UI_STATE, { model: Fragment.Change, change -> model.reduce(change) })
                .doOnNext { model: M -> Timber.v(" $model") }
    }

    override fun uiEvents(uiEvent: Fragment.UiEvent) {
        uiEvents.offer(uiEvent)
    }

    override fun uiModels(): Observable<Fragment.UiModel> = uiModel

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun mapUiEvent(uiEvent: Fragment.UiEvent) =
            when (uiEvent) {
                Fragment.UiEvent.ContentRefreshRequested -> Observable.just(Fragment.Change.ContentRefreshRequested)
            }

    private fun mapDataEvent(dataEvent: Fragment.DataEvent): Fragment.Change =
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
                Fragment.Change.ErrorIndicatorDismissed -> fromErrorIndicatorDismissedChange()
                Fragment.Change.ContentRefreshRequested -> from
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
