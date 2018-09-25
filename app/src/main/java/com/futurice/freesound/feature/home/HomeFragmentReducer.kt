package com.futurice.freesound.feature.home

import com.futurice.freesound.feature.common.streams.Fetch
import com.futurice.freesound.feature.common.streams.Operation
import com.futurice.freesound.mvi.Reducer
import com.futurice.freesound.network.api.model.User
import timber.log.Timber

class HomeFragmentReducer : Reducer<Result, HomeUiModel> {

    override fun reduce(current: HomeUiModel, result: Result) : HomeUiModel {
       return current.reduceModel(result)
    }

    private fun HomeUiModel.reduceModel(result: Result): HomeUiModel =
            when (result) {
                is Result.NoChange -> this
                is Result.UserUpdated -> reduce(result.userFetch)
                is Result.Refreshed -> reduce(result.refresh)
                Result.ErrorCleared -> copy(errorMsg = null)
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