package com.futurice.freesound.mvi

interface Reducer<R, S> {

    fun reduce(current : S, result : R): S
}