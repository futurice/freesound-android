package com.futurice.freesound.feature.home.user

import com.futurice.freesound.arch.mvi.Dispatcher
import com.futurice.freesound.arch.mvi.TransitionObserver
import com.futurice.freesound.arch.mvi.combine
import com.futurice.freesound.arch.mvi.viewmodel.ReducerViewModel
import com.futurice.freesound.arch.mvi.viewmodel.asUiModelFlowable
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.network.api.model.User
import io.reactivex.FlowableTransformer

sealed class HomeUiEvent {
    object LoadHomeUserRequested : HomeUiEvent()
    object ErrorIndicatorDismissed : HomeUiEvent()
    object RefreshRequested : HomeUiEvent()
}

sealed class HomeUiResult {
    object NoChange : HomeUiResult()
    object ErrorCleared : HomeUiResult()
    data class Refreshed(val refresh: Operation) : HomeUiResult()
    data class UserUpdated(val userFetch: Fetch<User>) : HomeUiResult()
}

sealed class HomeUiAction {
    object LoadHomeUser : HomeUiAction()
    object ClearError : HomeUiAction()
    object RefreshContent : HomeUiAction()
}

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val isRefreshing: Boolean,
                       val errorMsg: String?)

class HomeFragmentViewModel(private val homeUserInteractor: HomeUserInteractor,
                            private val refreshInteractor: RefreshInteractor,
                            schedulerProvider: SchedulerProvider,
                            transitionObserver: TransitionObserver)
    : ReducerViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>
(HomeUiEvent.LoadHomeUserRequested, schedulerProvider, transitionObserver, "HomeFragmentViewModel") {

    // FIXME This is the one piece of boilerplate remaiing.
    init {
        bind()
    }

    override fun initialUiState(): HomeUiModel = HomeUiModel(
            user = null,
            isLoading = false,
            isRefreshing = false,
            errorMsg = null)

    override fun initialEvent(): HomeUiEvent = HomeUiEvent.LoadHomeUserRequested

    override fun mapEvent(event: HomeUiEvent): HomeUiAction =
            when (event) {
                HomeUiEvent.LoadHomeUserRequested -> HomeUiAction.LoadHomeUser
                HomeUiEvent.RefreshRequested -> HomeUiAction.RefreshContent
                HomeUiEvent.ErrorIndicatorDismissed -> HomeUiAction.ClearError
            }

    override fun dispatch(): Dispatcher<HomeUiAction, HomeUiResult> = combine(
            loadHomeUser(),
            refresh(),
            clearError()
    )

    private fun clearError(): FlowableTransformer<in HomeUiAction, out HomeUiResult> =
            Dispatcher {
                it.ofType(HomeUiAction.ClearError::class.java)
                        .map { HomeUiResult.ErrorCleared }
            }

    private fun refresh(): FlowableTransformer<in HomeUiAction, out HomeUiResult> =
            Dispatcher {
                it.ofType(HomeUiAction.RefreshContent::class.java)
                        .flatMap { refreshInteractor.refresh().asUiModelFlowable() }
                        .map { result -> HomeUiResult.Refreshed(result) }
            }


    private fun loadHomeUser(): FlowableTransformer<in HomeUiAction, out HomeUiResult> =
            Dispatcher {
                it.ofType(HomeUiAction.LoadHomeUser::class.java)
                        .flatMap { homeUserInteractor.homeUserStream().asUiModelFlowable() }
                        .map { result -> HomeUiResult.UserUpdated(result) }
            }


    override fun reduce() = reducer
}

