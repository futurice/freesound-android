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

package com.futurice.freesound.feature.home

import io.reactivex.Observable

// This could probably just be a file, it would nice to have symmetric way of doing this
internal class HomeFragmentInteractor(private val userDataModel: UserDataModel) {

    fun dataEvents(): Observable<Fragment.DataEvent> {
        return homeUserFetch()
    }

    private fun homeUserFetch(): Observable<Fragment.DataEvent> {
        return userDataModel.homeUser
                .map { Fragment.DataEvent.UserDataEvent(it) }
                .map { it as Fragment.DataEvent }
                .toObservable()
                .startWith(Fragment.DataEvent.UserFetchInProgressEvent)
                .onErrorResumeNext { e: Throwable ->
                    Observable.just(Fragment.DataEvent.UserFetchFailedEvent(e))
                }
    }
}