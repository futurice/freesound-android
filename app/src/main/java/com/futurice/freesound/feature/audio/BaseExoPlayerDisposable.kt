/*
 * Copyright 2020 Futurice GmbH
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

package com.futurice.freesound.feature.audio

import com.google.android.exoplayer2.ExoPlayer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Base class for making Observables from ExoPlayer callback events.
 *
 * Primary purpose is avoid overriding all the ExoPlayer Player callbacks in each Disposable
 * and encapsulating the Disposable handling.
 *
 * TODO Could this derive from MainThreadDisposable instead?
 * TODO This could be made more abstract and provide an abstract onDisposed() hook.
 **/
internal abstract class BaseExoPlayerDisposable(protected val exoPlayer: ExoPlayer)
    : Disposable, SimplePlayerEventListener() {

    private val unsubscribed = AtomicBoolean()

    override fun dispose() {
        if (unsubscribed.compareAndSet(false, true)) {
            exoPlayer.removeListener(this)
        }
    }

    override fun isDisposed(): Boolean {
        return unsubscribed.get()
    }
}
