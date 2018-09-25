package com.futurice.freesound.feature.home

import com.futurice.freesound.mvi.ActionTransformer
import com.futurice.freesound.mvi.asUiModelFlowable
import io.reactivex.FlowableTransformer

class HomeFragmentActionTransformer(private val homeUserInteractor: HomeUserInteractor,
                                    private val refreshInteractor: RefreshInteractor) : ActionTransformer<Action, Result>() {

    override fun transform(): FlowableTransformer<in Action, out Result> {

        val initialEpic = FlowableTransformer<Action, Result> {
            it.ofType(Action.Initial::class.java)
                    .flatMap { homeUserInteractor.homeUserStream().asUiModelFlowable() }
                    .map { Result.UserUpdated(it) }
        }

        val refreshEpic = FlowableTransformer<Action, Result> {
            it.ofType(Action.RefreshContent::class.java)
                    .flatMap { refreshInteractor.refresh().asUiModelFlowable() }
                    .map { Result.Refreshed(it) }
        }

        val dismissErrorIndicatorEpic = FlowableTransformer<Action, Result> {
            it.ofType(Action.ClearError::class.java)
                    .map { Result.ErrorCleared }
        }

        return merge(initialEpic, refreshEpic, dismissErrorIndicatorEpic)
    }
}