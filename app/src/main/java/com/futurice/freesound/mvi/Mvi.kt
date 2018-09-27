package com.futurice.freesound.mvi

interface Event
interface Action
interface Result
interface State

typealias EventMapper<E, A> = (E) -> A
