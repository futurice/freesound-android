package com.futurice.freesound.arch.mvi.store

import com.futurice.freesound.arch.mvi.*
import io.reactivex.FlowableTransformer

class Store<R : Result, S : State>(
        private val initialState: S,
        private val reducer: Reducer<R, S>,
        private val tag: String,
        private val logger: Logger) {

    fun reduce(): FlowableTransformer<R, S> {
        return FlowableTransformer { it ->
            it.scan(initialState) { model: S, result: R -> reduce(model, result) }
        }
    }

    private fun reduce(current: S, result: R): S {
        logger.log(tag, LogEvent.Reduce(result, current))
        return reducer(current, result)
    }

}
