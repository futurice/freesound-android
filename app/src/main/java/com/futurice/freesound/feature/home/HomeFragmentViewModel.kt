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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.mvi.BaseViewModel
import com.futurice.freesound.mvi.asUiEventFlowable
import com.futurice.freesound.mvi.asUiModelFlowable
import com.futurice.freesound.network.api.model.User
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

internal class HomeFragmentViewModel(private val homeUserInteractor: HomeUserInteractor,
                                     private val refreshInteractor: RefreshInteractor,
                                     schedulers: SchedulerProvider) : BaseViewModel<UiEvent, HomeUiModel>() {

    private val disposable = SerialDisposable()

    private val uiEvents: PublishSubject<UiEvent> = PublishSubject.create()

    private val uiModel = MutableLiveData<HomeUiModel>()

    private val uiModelsStream: Flowable<HomeUiModel> = uiEvents
            .observeOn(schedulers.computation()) // because subscribeOn doesn't work for Subjects
            .startWith(UiEvent.InitialEvent)
            .asUiEventFlowable()
            .map(::toAction)
            .compose(transformToResult())
            .scan(INITIAL_UI_STATE, { model, result -> model.reduce(result) })
            .doOnNext { homeUiModel: HomeUiModel -> Timber.v(" $homeUiModel") }
            .doOnError { e: Throwable -> Timber.e(e, "An unexpected fatal error occurred in $javaClass") }
            .asUiModelFlowable()
            .subscribeOn(schedulers.computation())

    private val INITIAL_UI_STATE: HomeUiModel
        get() = HomeUiModel(
                user = null,
                isLoading = false,
                isRefreshing = false,
                errorMsg = null)

    init {
        disposable.set(uiModelsStream
                .observeOn(schedulers.ui())
                .subscribe(
                        { uiModel.value = it },
                        { Timber.e(it, "Disaster has occurred.") }))
    }

    override fun uiEvents(uiEvent: UiEvent) {
        uiEvents.onNext(uiEvent)
    }

    override fun uiModels(): LiveData<HomeUiModel> {
        return uiModel
    }

    override fun onCleared() {
        disposable.dispose()
    }

    private fun toAction(uiEvent: UiEvent) =
            when (uiEvent) {
                UiEvent.InitialEvent -> Action.InitialAction
                UiEvent.RefreshRequested -> Action.ContentRefreshAction
                UiEvent.ErrorIndicatorDismissed -> Action.ErrorClearAction
            }.also { Timber.d("From UiEvent: $uiEvent, Action is: $it") }

    private fun transformToResult(): FlowableTransformer<Action, out Result> {

        val initial:
                FlowableTransformer<Action.InitialAction, Result.UserResult> =
                FlowableTransformer {
                    it.flatMap {
                        homeUserInteractor.homeUserStream().asUiModelFlowable()
                    }.map { Result.UserResult(it) }
                }

        val refresh:
                FlowableTransformer<Action.ContentRefreshAction, Result.RefreshResult> =
                FlowableTransformer {
                    it.flatMap {
                        refreshInteractor.refresh().asUiModelFlowable()
                    }.map { Result.RefreshResult(it) }
                }

        val dismissErrorIndicator:
                FlowableTransformer<Action.ErrorClearAction, Result.ErrorClearedResult> =
                FlowableTransformer { it.map { Result.ErrorClearedResult } }

        return FlowableTransformer {
            it.publish { shared: Flowable<Action> ->
                Flowable.merge(
                        shared.ofType(Action.InitialAction::class.java).compose(initial),
                        shared.ofType(Action.ContentRefreshAction::class.java).compose(refresh),
                        shared.ofType(Action.ErrorClearAction::class.java).compose(dismissErrorIndicator))
            }
        }

    }

    private fun HomeUiModel.reduce(result: Result): HomeUiModel =
            when (result) {
                is Result.NoChange -> this
                is Result.UserResult -> reduce(result.userFetch)
                is Result.RefreshResult -> reduce(result.refresh)
                Result.ErrorClearedResult -> copy(errorMsg = null)
            }.also { Timber.d("Result: $result was reduced to: $it") }

    private fun HomeUiModel.reduce(refresh: Operation): HomeUiModel =
            when (refresh) {
                Operation.InProgress -> copy(isRefreshing = true, errorMsg = null)
                Operation.Complete -> copy(isRefreshing = false, errorMsg = null)
                is Operation.Failure -> copy(isRefreshing = false, errorMsg = toFetchFailureMsg(refresh.error))
            }.also { Timber.d("Operation: $refresh was reduced to: $it") }

    private fun HomeUiModel.reduce(fetch: Fetch<User>): HomeUiModel =
            when (fetch) {
                is Fetch.InProgress -> copy(isLoading = user == null, errorMsg = null)
                is Fetch.Success<User> -> copy(user = toUserUiModel(fetch.value), isLoading = false)
                is Fetch.Failure -> copy(errorMsg = toFetchFailureMsg(fetch.error), isLoading = false)
            }.also { Timber.d("Fetch: $fetch was reduced to: $it") }


    // TODO This could return an @ResId instead of a string. Issues with config changes e.g. language?
    private fun toFetchFailureMsg(throwable: Throwable) = throwable.localizedMessage

    private fun toUserUiModel(user: User) =
            UserUiModel(user.username, about = user.about, avatarUrl = user.avatar.large)

}

sealed class Result(val log: String) {
    object NoChange : Result("No-op change")
    object ErrorClearedResult : Result("Error dismissed change")
    data class RefreshResult(val refresh: Operation) : Result("User Fetch state change: $refresh")
    data class UserResult(val userFetch: Fetch<User>) : Result("User state change: $userFetch")
}

sealed class Action(val log: String) {
    object InitialAction : Action("Initial action")
    object ErrorClearAction : Action("Error cleared action")
    object ContentRefreshAction : Action("Content refresh action")
}
