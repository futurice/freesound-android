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
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

sealed class Result(val log: String) {
    object NoChange : Result("No-op change")
    object ErrorClearedResult : Result("Error dismissed change")
    class UserResult(val fetch: Fetch<User>) : Result("User Fetch state change")
}

sealed class Action(val log: String) {
    object ErrorClearAction : Action("Error cleared action")
    object ContentRefreshAction : Action("Content refresh action")
}

internal class HomeFragmentViewModel2(private val homeUserInteractor: HomeUserInteractor,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<UiEvent, HomeUiModel>(schedulers) {

    // Events from the UI are modelled as a buffering Observable.
    private val uiEvents: PublishSubject<UiEvent> = PublishSubject.create()

    // TODO This initial state doesn't make sense - it should be driven by an input configuration (e.g. an id, which be serialized),
    // not a result. Think about what will happen with a list restored from onSaveInstanceState
    private val INITIAL_UI_STATE: HomeUiModel get() = HomeUiModel(null, false, null)

    override fun uiModels(): Flowable<HomeUiModel> {
        return uiEvents
                .toFlowable(BackpressureStrategy.BUFFER)
                .map(::toAction)
                .compose(toResult())
                .scan(INITIAL_UI_STATE, { model, result -> model.reduce(result) })
                .doOnNext { homeUiModel: HomeUiModel -> Timber.v(" $homeUiModel") }
                .doOnError { e: Throwable -> Timber.e(e, "An unexpected fatal error occurred in $javaClass") }
                .subscribeOn(schedulers.computation())
                .onBackpressureLatest()
        //.onErrorResume { e: Throwable -> Flowable.just(HomeUiModel.Error(e)) }
    }

    private fun toAction(uiEvent: UiEvent) =
            when (uiEvent) {
                UiEvent.RefreshRequested -> Action.ContentRefreshAction
                UiEvent.ErrorIndicatorDismissed -> Action.ErrorClearAction
            }

    private fun toResult(): FlowableTransformer<Action, out Result> {

        val refresh:
                FlowableTransformer<Action.ContentRefreshAction, Result.UserResult> =
                FlowableTransformer {
                    it.flatMap { homeUserInteractor.homeUser() }.map { Result.UserResult(it) }
                }

        val dismissErrorIndicator:
                FlowableTransformer<Action.ErrorClearAction, Result.ErrorClearedResult> =
                FlowableTransformer { it.map { Result.ErrorClearedResult } }

        // Compiles, but ugly. This is the JW suggested approach.
//        return FlowableTransformer {
//            it.publish { shared: Observable<Action> ->
//                Observable.merge(shared.ofType(Action.ContentRefreshAction::class.java).compose(refresh),
//                        shared.ofType(Action.ErrorClearAction::class.java).compose(dismissErrorIndicator))
//            }
//        }

        return FlowableTransformer { actions ->
            // TODO This definitely needs testing to verify that we have a merged signal and that
            // changes in the input action don't cancel previous events.
            actions.flatMap {
                when (it) {
                //      is Action.ContentRefreshAction -> actions.map { it as Action.ContentRefreshAction }.compose(refresh)
                //    is Action.ErrorClearAction -> actions.map { it as Action.ErrorClearAction }.compose(dismissErrorIndicator)
                //   is Action.ContentRefreshAction -> actions.composeAs(refresh)
                //   is Action.ErrorClearAction -> actions.composeAs(dismissErrorIndicator)
                    is Action.ContentRefreshAction -> Flowable.just(it).compose(refresh)
                    is Action.ErrorClearAction -> Flowable.just(it).compose(dismissErrorIndicator)
                }
            }
        }

    }

//     @Suppress("UNCHECKED_CAST")
//    private fun <A, A2 : A, R> Observable<A>.composeAs(transformer: FlowableTransformer<A2, R>): Observable<R> {
//        return this.map { it as A2 }.compose(transformer)
//    }

    override fun uiEvents(uiEvent: UiEvent) {
        uiEvents.onNext(uiEvent)
    }

    private fun HomeUiModel.reduce(result: Result): HomeUiModel =
            when (result) {
                is Result.NoChange -> this
                is Result.UserResult -> reduceUserChange(result.fetch)
                Result.ErrorClearedResult -> copy(errorMsg = null)
            }

    private fun HomeUiModel.reduceUserChange(fetch: Fetch<User>): HomeUiModel =
            when (fetch) {
                is Fetch.InProgress<User> -> copy(isLoading = true, user = null, errorMsg = null)
                is Fetch.Success<User> -> copy(user = toUserUiModel(fetch.value), isLoading = false, errorMsg = null)
                is Fetch.Failure<User> -> copy(isLoading = false, errorMsg = toFetchFailureMsg(fetch.error))
            }

    // TODO Perhaps this could return an @ResId instead of a string. Issues with config changes e.g. language?
    private fun toFetchFailureMsg(throwable: Throwable) = throwable.localizedMessage

    private fun toUserUiModel(user: User): UserUiModel =
            UserUiModel(user.username, about = user.about, avatarUrl = user.avatar.large)

}
