package com.futurice.freesound.arch.mvi

import io.reactivex.Flowable
import io.reactivex.FlowableTransformer

interface Event
interface Action
interface Result
interface State

typealias EventMapper<E, A> = (E) -> A

typealias Reducer<R, S> = (S, R) -> S

typealias Dispatcher<A, R> = FlowableTransformer<in A, out R>

fun <A, R> combine(vararg transformers: Dispatcher<A, R>): Dispatcher<A, R> {
    return Dispatcher {
        it.publish { actions: Flowable<A> ->
            Flowable.merge(transformers.map { it -> actions.compose(it) })
        }
    }
}
