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

import com.futurice.freesound.feature.common.Fetch
import com.futurice.freesound.feature.common.Operation
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.mvi.BaseViewModel
import com.futurice.freesound.network.api.model.User
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

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

internal class HomeFragmentViewModel2(private val homeHomeUserInteractor: HomeUserInteractor,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<UiEvent, HomeUiModel>(schedulers) {

    // Events from the UI are modelled as a buffering Observable.
    private val uiEvents: PublishSubject<UiEvent> = PublishSubject.create()

    // TODO Use InitialEvent to hold saveInstanceState data.
    private val INITIAL_UI_STATE: HomeUiModel get() = HomeUiModel(null, false, null)

    //  No caching currently in the ViewModel, uses data sources to hold results.
    // Could change this to output to a ViewModel scoped LiveData object.
    // Or we could just rely on caching of the source objects in the Repository.
    override fun uiModels(): Flowable<HomeUiModel> {
        return uiEvents
                .observeOn(schedulers.computation()) // because subscribeOn doesn't work for Subjects
                .startWith(UiEvent.InitialEvent)
                .toFlowable(BackpressureStrategy.BUFFER)
                .map(::toAction)
                .compose(toResult())
                .scan(INITIAL_UI_STATE, { model, result -> model.reduce(result) })
                .doOnNext { homeUiModel: HomeUiModel -> Timber.v(" $homeUiModel") }
                .doOnError { e: Throwable -> Timber.e(e, "An unexpected fatal error occurred in $javaClass") }
                .onBackpressureLatest()
                .subscribeOn(schedulers.computation())
        //.onErrorResume { e: Throwable -> Flowable.just(HomeUiModel.Error(e)) }
    }

    private fun toAction(uiEvent: UiEvent) =
            when (uiEvent) {
                UiEvent.InitialEvent -> Action.InitialAction
                UiEvent.RefreshRequested -> Action.ContentRefreshAction
                UiEvent.ErrorIndicatorDismissed -> Action.ErrorClearAction
            }.also { Timber.d("Action is: $it") }

    private fun toResult(): FlowableTransformer<Action, out Result> {

        val initial:
                FlowableTransformer<Action.InitialAction, Result.UserResult> =
                FlowableTransformer {
                    it.doOnNext { Timber.d("Processing action: $it") }
                            .flatMap {
                                homeHomeUserInteractor.homeUserStream()
                                        .doOnEach { Timber.d("Homeuserstream has event: $it") }
                                        .toUiModelFlowable()
                            }.map { Result.UserResult(it) }
                }

        val refresh:
                FlowableTransformer<Action.ContentRefreshAction, Result.RefreshResult> =
                FlowableTransformer {
                    it.flatMap {
                        homeHomeUserInteractor.refresh().toUiModelFlowable()
                    }.map { Result.RefreshResult(it) }
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

        return FlowableTransformer {
            it.publish { shared: Flowable<Action> ->
                Flowable.merge(shared.ofType(Action.ContentRefreshAction::class.java).compose(refresh),
                        shared.ofType(Action.ErrorClearAction::class.java).compose(dismissErrorIndicator),
                        shared.ofType(Action.InitialAction::class.java).compose(initial))
            }
        }

//        return FlowableTransformer { actions ->
//            // TODO This definitely needs testing to verify that we have a merged signal and that
//            // changes in the input action don't cancel previous events.
//            actions.flatMap {
//                when (it) {
//                //      is Action.ContentRefreshAction -> actions.map { it as Action.ContentRefreshAction }.compose(refresh)
//                //    is Action.ErrorClearAction -> actions.map { it as Action.ErrorClearAction }.compose(dismissErrorIndicator)
//                //   is Action.ContentRefreshAction -> actions.composeAs(refresh)
//                //   is Action.ErrorClearAction -> actions.composeAs(dismissErrorIndicator)
//                    is Action.InitialAction -> Flowable.just(it).compose(initial)
//                    is Action.ContentRefreshAction -> Flowable.just(it).compose(refresh)
//                    is Action.ErrorClearAction -> Flowable.just(it).compose(dismissErrorIndicator)
//                }
//            }
//        }

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
                is Result.UserResult -> reduce(result.userFetch)
                is Result.RefreshResult -> reduce(result.refresh)
                Result.ErrorClearedResult -> copy(errorMsg = null)
            }.also { Timber.d("Result was: $result") }

    private fun HomeUiModel.reduce(fetch: Operation): HomeUiModel =
            when (fetch) {
                Operation.InProgress -> copy(isLoading = this.user == null, user = null, errorMsg = null)
                Operation.Complete -> copy(isLoading = false, errorMsg = null)
                is Operation.Failure -> copy(isLoading = false, errorMsg = toFetchFailureMsg(fetch.error))
            }

    // TODO Perhaps this could return an @ResId instead of a string. Issues with config changes e.g. language?
    private fun toFetchFailureMsg(throwable: Throwable) = throwable.localizedMessage

    private fun HomeUiModel.reduce(fetch: Fetch<User>): HomeUiModel =
            when (fetch) {
                is Fetch.InProgress -> copy(isLoading = user == null)
                is Fetch.Success<User> -> copy(user = toUserUiModel(fetch.value), isLoading = false)
                is Fetch.Failure -> copy(errorMsg = toFetchFailureMsg(fetch.error), isLoading = false)
            }

    private fun toUserUiModel(user: User) =
            UserUiModel(user.username, about = user.about, avatarUrl = user.avatar.large)

}

// For consumable values, we just take the latest if backpressure.
private fun <T> Observable<T>.toUiModelFlowable(): Flowable<T> {
    return toFlowable(BackpressureStrategy.LATEST)
}
