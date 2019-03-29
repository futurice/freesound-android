package com.futurice.freesound.feature.home.user

import com.futurice.freesound.arch.mvi.*
import com.futurice.freesound.arch.mvi.viewmodel.BaseViewModel
import com.futurice.freesound.arch.mvi.viewmodel.MviViewModel
import com.futurice.freesound.arch.mvi.viewmodel.asUiModelFlowable
import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.network.api.model.User

sealed class HomeUiEvent : Event {
    object Initial : HomeUiEvent()
    object ErrorIndicatorDismissed : HomeUiEvent()
    object RefreshRequested : HomeUiEvent()
}

sealed class HomeUiResult : Result {
    object NoChange : HomeUiResult()
    object ErrorCleared : HomeUiResult()
    data class Refreshed(val refresh: Operation) : HomeUiResult()
    data class UserUpdated(val userFetch: Fetch<User>) : HomeUiResult()
}

sealed class HomeUiAction : Action {
    object Initial : HomeUiAction()
    object ClearError : HomeUiAction()
    object RefreshContent : HomeUiAction()
}

data class UserUiModel(val username: String,
                       val about: String,
                       val avatarUrl: String)

data class HomeUiModel(val user: UserUiModel?,
                       val isLoading: Boolean,
                       val isRefreshing: Boolean,
                       val errorMsg: String?) : State

const val LOG_TAG = "HomeUserUi"

val INITIAL_UI_EVENT = HomeUiEvent.Initial

val INITIAL_UI_STATE: HomeUiModel
    get() = HomeUiModel(
            user = null,
            isLoading = false,
            isRefreshing = false,
            errorMsg = null)

val eventMapper: EventMapper<HomeUiEvent, HomeUiAction> =
        { uiEvent: HomeUiEvent ->
            when (uiEvent) {
                HomeUiEvent.Initial -> HomeUiAction.Initial
                HomeUiEvent.RefreshRequested -> HomeUiAction.RefreshContent
                HomeUiEvent.ErrorIndicatorDismissed -> HomeUiAction.ClearError
            }
        }

fun dispatcher(homeUserInteractor: HomeUserInteractor,
               refreshInteractor: RefreshInteractor): Dispatcher<HomeUiAction, HomeUiResult> {

    val initial = Dispatcher<HomeUiAction, HomeUiResult> {
        it.ofType(HomeUiAction.Initial::class.java)
                .flatMap { homeUserInteractor.homeUserStream().asUiModelFlowable() }
                .map { HomeUiResult.UserUpdated(it) }
    }

    val refresh = Dispatcher<HomeUiAction, HomeUiResult> {
        it.ofType(HomeUiAction.RefreshContent::class.java)
                .flatMap { refreshInteractor.refresh().asUiModelFlowable() }
                .map { HomeUiResult.Refreshed(it) }
    }

    val dismissErrorIndicator = Dispatcher<HomeUiAction, HomeUiResult> {
        it.ofType(HomeUiAction.ClearError::class.java)
                .map { HomeUiResult.ErrorCleared }
    }

    return combine(initial, refresh, dismissErrorIndicator)
}
//@AssistedInject.Factory
//interface Factory {
//    fun create(initialState: HomeUiEvent): HomeFragmentViewModel
//}

class HomeFragmentViewModel(baseViewModel: BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>)
    : MviViewModel<HomeUiEvent, HomeUiModel> by baseViewModel {

//    companion object : MviViewModelFactory<HomeUiEvent, HomeUiModel> {
//        @JvmStatic
//        override fun create(fragment: Fragment, initialEvent: HomeUiEvent): HomeFragmentViewModel {
//            return (fragment as HomeFragment).episodeDetailsViewModelFactory.create(state)
//        }
//    }
}
