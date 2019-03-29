package com.futurice.freesound.arch.mvi

import android.support.v4.app.Fragment
import com.futurice.freesound.arch.mvi.viewmodel.MviViewModel

interface MviViewModelFactory<E, M> {

    fun create(fragment: Fragment, initialEvent: E): MviViewModel<E, M>
}
