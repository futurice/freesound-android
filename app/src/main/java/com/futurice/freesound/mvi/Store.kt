package com.futurice.freesound.mvi

import io.reactivex.FlowableTransformer
import timber.log.Timber

class Store<S, A, R>(
        private val tag: String,
        private val initialState: S,
        private val actionTransformer: ActionTransformer<A, R>,
        private val reducer: Reducer<R, S>) {

    fun dispatchAction(): FlowableTransformer<A, S> {
        return FlowableTransformer { it ->
            it.compose(actionTransformer.transform())
                    .scan(initialState) { model: S, result: R -> reducer.reduce(model, result) }
                    .doOnNext { model: S -> Timber.v(" $model") }
                    .doOnError { e: Throwable -> Timber.e(e, "An unexpected fatal error occurred in $tag") }
                    .asUiModelFlowable()
        }
    }

}
