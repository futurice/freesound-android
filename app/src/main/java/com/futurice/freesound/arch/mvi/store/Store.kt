package com.futurice.freesound.arch.mvi.store

import com.futurice.freesound.arch.mvi.*
import com.futurice.freesound.arch.mvi.viewmodel.asUiModelFlowable
import io.reactivex.FlowableTransformer

class Store<A : Action, R : Result, S : State>(
        private val initialState: S,
        private val actionTransformer: ActionTransformer<A, R>,
        private val reducer: Reducer<R, S>,
        private val tag: String,
        private val logger: Logger) {

    fun dispatchAction(): FlowableTransformer<A, S> {
        return FlowableTransformer { it ->
            it.compose(actionTransformer)
                    .doOnNext { result -> logger.log(tag, LogEvent.Result(result)) }
                    .scan(initialState) { model: S, result: R -> reduce(model, result) }
                    .doOnNext { model: S -> logger.log(tag, LogEvent.State(model)) }
                    .asUiModelFlowable()
        }
    }

    private fun reduce(current: S, result: R): S {
        logger.log(tag, LogEvent.Reduce(result, current))
        return reducer(current, result)
    }

}
