package com.futurice.freesound.arch.mvi.store

import com.futurice.freesound.arch.mvi.*
import com.futurice.freesound.arch.mvi.viewmodel.asUiModelFlowable
import io.reactivex.FlowableTransformer

class Store<R : Result, S : State>(
        private val initialState: S,
        private val reducer: Reducer<R, S>,
        private val tag: String,
        private val logger: Logger) {

    fun reduceResult(): FlowableTransformer<R, S> {
        return FlowableTransformer { it ->
            it.doOnNext { result -> logger.log(tag, LogEvent.Result(result)) }
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
