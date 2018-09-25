package com.futurice.freesound.feature.home

import com.futurice.freesound.mvi.ActionTransformer
import com.futurice.freesound.mvi.asUiModelFlowable
import io.reactivex.FlowableTransformer

class HomeFragmentActionTransformer(private val homeUserInteractor: HomeUserInteractor,
                                    private val refreshInteractor: RefreshInteractor) : ActionTransformer<HomeUiAction, HomeUiResult>() {

    override fun transform(): FlowableTransformer<in HomeUiAction, out HomeUiResult> {

        val initialEpic = FlowableTransformer<HomeUiAction, HomeUiResult> {
            it.ofType(HomeUiAction.Initial::class.java)
                    .flatMap { homeUserInteractor.homeUserStream().asUiModelFlowable() }
                    .map { HomeUiResult.UserUpdated(it) }
        }

        val refreshEpic = FlowableTransformer<HomeUiAction, HomeUiResult> {
            it.ofType(HomeUiAction.RefreshContent::class.java)
                    .flatMap { refreshInteractor.refresh().asUiModelFlowable() }
                    .map { HomeUiResult.Refreshed(it) }
        }

        val dismissErrorIndicatorEpic = FlowableTransformer<HomeUiAction, HomeUiResult> {
            it.ofType(HomeUiAction.ClearError::class.java)
                    .map { HomeUiResult.ErrorCleared }
        }

        return merge(initialEpic, refreshEpic, dismissErrorIndicatorEpic)
    }
}