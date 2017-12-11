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
import com.futurice.freesound.mvi.BaseViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber

internal class HomeFragmentViewModel2(private val homeUserInteractor: HomeUserInteractor,
                                      schedulers: SchedulerProvider) :
        BaseViewModel<HomeFragmentUiEvent, HomeFragmentUiModel>(schedulers) {

    private val uiEvents: PublishProcessor<HomeFragmentUiEvent> = PublishProcessor.create()
    private val uiModel: MutableLiveData<HomeFragmentUiModel> = MutableLiveData()
    private val disposable: Disposable

    val INITIAL_UI_STATE: HomeFragmentUiModel get() = HomeFragmentUiModel(null, false, null)

    init {
        disposable = (defineUiModel()
                .subscribeOn(schedulers.computation())
                .subscribe({ uiModel.postValue(it) },
                        { e -> Timber.e(e, "Fatal error in ${HomeFragmentViewModel2::javaClass}") }))
    }

    private fun defineUiModel(): Observable<HomeFragmentUiModel> {
        return uiEvents
                .compose(transform())
                .scan(INITIAL_UI_STATE, { model: Fragment.Change, change -> model.reduce(change) })
                .doOnNext { model: M -> Timber.v(" $model") }
    }

    /**
     * The transform takes the event from the ui.
     *
     * They will be split depending on their type (when) ... and the observable transformed to
     * a change observable.
     *
     * Take UIEvents .. [[map that to an Action] .. [map that to a Result]] ... to a Change .. to a Model
     *
     * uiModel = uiEvents.compose(transform()).merge(fetchHomeUser.compose(tranform())).defineUiModel()
     * uiModel = uibasedchanges + databasedchanges
     *
     * Is there a way to model dataChanges as a function of ui visibility for the situations where
     * there are changes that aren't driven by obvious user events from the ui.
     *
     * perhaps that could be pulled into the parent class. Always have an event to do with "subscribing".
     */
    fun transform(): ObservableTransformer<HomeFragmentUiEvent, Fragment.Change> {

        // TODO Pull this out into a separate UseCase/Domain
        val fetchUser:
                ObservableTransformer<UserAction.Fetch, FetchUserResult> =
                ObservableTransformer {
                    homeUserInteractor.fetchHomeUser()
                            .map { FetchUserResult.UserDataEvent(it) }
                            .map { it as FetchUserResult }
                            .startWith(FetchUserResult.UserFetchInProgressEvent)
                            .onErrorResumeNext { e: Throwable ->
                                Observable.just(FetchUserResult.UserFetchFailureEvent(e))
                            }
                }

        val contentRefreshRequested: ObservableTransformer<HomeFragmentUiEvent.ContentRefreshRequested,
                Fragment.Change.ContentRefreshRequested>
                = ObservableTransformer { it.map { UserAction.Fetch }
                    .compose(fetchUser)
                    .map { }
        }

        val dismissErrorIndicator:
                ObservableTransformer<HomeFragmentUiEvent.ErrorIndicatorDismissed,
                        Fragment.Change.ErrorIndicatorDismissed> =
                ObservableTransformer {
                    Observable.just(Fragment.Change.ErrorIndicatorDismissed)
                }

        return ObservableTransformer {
            when (it) {
                is HomeFragmentUiEvent.ErrorIndicatorDismissed -> dismissErrorIndicator
                is HomeFragmentUiEvent.ContentRefreshRequested -> contentRefreshRequested

            }
        }
    }


    override fun uiEvents(uiEvent: HomeFragmentUiEvent) {
        uiEvents.offer(uiEvent)
    }

    override fun uiModels(): LiveData<HomeFragmentUiModel> = uiModel

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun HomeFragmentUiModel.defineUiModel(change: Fragment.Change): HomeFragmentUiModel =
            when (change) {
                is Fragment.Change.NoChange -> this
                Fragment.Change.UserFetchInProgressChanged -> copy(isLoading = true)
                is Fragment.Change.UserChanged -> fromUserChanged(change)
                is Fragment.Change.UserFetchErrorChanged -> copy(isLoading = false, errorMsg = change.errorMsg)
                Fragment.Change.ErrorIndicatorDismissed -> copy(errorMsg = null)
                Fragment.Change.ContentRefreshRequested -> from
            }


    private fun mapUiEvent(uiEvent: HomeFragmentUiEvent) =
            when (uiEvent) {
                HomeFragmentUiEvent.ContentRefreshRequested -> Observable.just(Fragment.Change.ContentRefreshRequested)
            }

    private fun mapDataEvent(dataEvent: Fragment.DataEvent): Fragment.Change =
            when (dataEvent) {
                Fragment.DataEvent.UserFetchInProgressEvent -> Fragment.Change.UserFetchInProgressChanged
                is Fragment.DataEvent.UserDataEvent -> Fragment.Change.UserChanged(dataEvent.user)
                is Fragment.DataEvent.UserFetchFailedEvent -> Fragment.Change.UserFetchErrorChanged(dataEvent.error.localizedMessage)
            }


    private fun HomeFragmentUiModel.fromUserChanged(change: Fragment.Change.UserChanged): HomeFragmentUiModel
            = copy(user = Fragment.UserUiModel(change.user.username(),
            about = change.user.about(),
            avatarUrl = change.user.avatar().large()),
            isLoading = false,
            errorMsg = null)

}
