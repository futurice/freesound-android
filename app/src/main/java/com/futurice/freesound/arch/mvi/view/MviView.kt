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

package com.futurice.freesound.arch.mvi.view

import android.arch.lifecycle.LiveData
import com.futurice.freesound.arch.mvi.Event
import com.futurice.freesound.arch.mvi.State

interface MviView<E : Event, in M : State> {
    fun uiEvents(): LiveData<E> = EmptyLiveData()
    fun render(model: M)
    fun cancel() {}
}

interface Renderer<in M : State> {
    fun render(model: M)
    fun cancel() {}
}

private class EmptyLiveData<T> : LiveData<T>()
