/*
 * Copyright 2018 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.arch.mvi.view

import android.support.v4.app.Fragment
import com.futurice.freesound.arch.core.BaseFragment
import com.futurice.freesound.arch.mvi.Event
import com.futurice.freesound.arch.mvi.State
import com.futurice.freesound.arch.mvi.viewmodel.MviViewModel

/**
 * A base Fragment which provides the binding mechanism hooks to a MviView Model.
 *
 * @param <C> The DI component class.
 */
abstract class MviBaseFragment<C, E : Event, M : State, VM : MviViewModel<E, M>> : BaseFragment<C>(), MviView<E, M> {

    private val vm: VM by flowFinder()

}

// What about requireActivity()

inline fun <T : Fragment, E : Event, M : State, reified VM : MviViewModel<E, M>> T.flowFinder()
       : VM  {
             // Need something to define the initial value when recreating
    return makeVM(VM::class.java)
            .apply {  }
             //return Flow<E, M, VM>(this, vm, this)

}

//    @Suppress("UNCHECKED_CAST")
fun <E : Event, M : State, VM : MviViewModel<E, M>> makeVM(viewModelClass: Class<VM>): VM {
        val method = try {
            viewModelClass.getMethod("create")
        } catch (exception: NoSuchMethodException) {
            null
        }
        return method?.invoke(null) as? VM
    }
}



