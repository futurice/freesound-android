package com.futurice.freesound.arch.mvi

import timber.log.Timber

class Logger : TransitionObserver {

    override fun onTransition(tag: String, transitionEvent: TransitionEvent) {
        when (transitionEvent) {
            is TransitionEvent.Event -> Timber.d("MVI|$tag| Event => $transitionEvent")
            is TransitionEvent.Action -> Timber.d("MVI|$tag| Action => $transitionEvent")
            is TransitionEvent.Result -> Timber.d("MVI|$tag| Result => $transitionEvent")
            is TransitionEvent.Reduce -> Timber.d("MVI|$tag| Reduce => $transitionEvent")
            is TransitionEvent.State -> Timber.d("MVI|$tag| State => $transitionEvent")
            is TransitionEvent.Error -> Timber.e(transitionEvent.throwable, "MVI|$tag| Fatal Error => $transitionEvent")
        }
    }
}
