/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.mvi

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer

/**
 * The MviView holds this instance.
 */
class UiBinder<M, E>(private val mviView: MviView<E, M>,
                     private val viewModel: ViewModel<E, M>,
                     lifecycleOwner: LifecycleOwner) {

    // LiveData thinks the events can be null, be we know better - they come from RxJava 2 sources

    init {
        mviView.uiEvents()
                .observe(lifecycleOwner, Observer { viewModel.uiEvents(it!!) })

        viewModel.uiModels()
                .observe(lifecycleOwner, Observer { mviView.render(it!!) })
    }

}