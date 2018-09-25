package com.futurice.freesound.mvi

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

abstract class ActionTransformer<A, R> {

    abstract fun transform(): FlowableTransformer<in A, out R>

    protected fun merge(vararg epics: FlowableTransformer<A, R>): FlowableTransformer<A, R> {
        return FlowableTransformer {
            it.publish { actions: Flowable<A> ->
                Flowable.merge(epics.map { it -> actions.compose(it) })
            }
        }
    }
}

