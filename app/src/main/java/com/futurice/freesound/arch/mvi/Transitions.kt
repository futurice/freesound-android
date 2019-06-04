package com.futurice.freesound.arch.mvi

interface TransitionObserver {

    fun onTransition(tag: String, transitionEvent: TransitionEvent)
}

sealed class TransitionEvent {
    data class Event(val event: Any) : TransitionEvent()
    data class Action(val action: Any) : TransitionEvent()
    data class Result(val result: Any) : TransitionEvent()
    data class Reduce(val result: Any, val prevState: Any) : TransitionEvent()
    data class State(val state: Any) : TransitionEvent()
    data class Error(val throwable: Throwable) : TransitionEvent()
}
