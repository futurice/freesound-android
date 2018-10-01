/*
 * Copyright 2016 Futurice GmbH
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

import com.futurice.freesound.common.rx.plusAssign
import com.futurice.freesound.feature.common.Navigator
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.arch.viewmodel.SimpleViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import polanski.option.Unit
import timber.log.Timber

internal class HomeViewModel(val navigator: Navigator,
                             val schedulerProvider: SchedulerProvider) : SimpleViewModel() {

    private val openSearchStream = PublishSubject.create<Unit>()

    fun openSearch() {
        openSearchStream.onNext(Unit.DEFAULT)
    }

    override fun bind(disposables: CompositeDisposable) {
        disposables +=
                openSearchStream.observeOn(schedulerProvider.ui())
                        .subscribe({ navigator.openSearch() },
                                { Timber.e(it, "Error opening search") })

    }
}
