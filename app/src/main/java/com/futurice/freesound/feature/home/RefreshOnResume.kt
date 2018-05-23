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

package com.futurice.freesound.feature.home

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

@Deprecated("This should be replaced by a parameterized component")
class RefreshOnResume(lifecycle: Lifecycle) : LifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    private val resumed: PublishProcessor<Unit> = PublishProcessor.create()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun refresh() {
        resumed.offer(Unit)
    }

    fun refreshRequests(): Flowable<Unit> {
        return resumed
    }

}