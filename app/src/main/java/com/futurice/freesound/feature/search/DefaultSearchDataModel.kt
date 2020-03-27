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
package com.futurice.freesound.feature.search

import com.futurice.freesound.common.utils.Preconditions
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.network.api.FreeSoundApiService
import com.futurice.freesound.network.api.model.Sound
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import polanski.option.Option
import polanski.option.OptionUnsafe

internal class DefaultSearchDataModel(private val freeSoundApiService: FreeSoundApiService,
                                      private val schedulerProvider: SchedulerProvider) : SearchDataModel {

    private val inProgressOnceAndStream: Subject<Boolean> =
            BehaviorSubject.createDefault(false)
    private val resultsOnceAndStream: Subject<Option<List<Sound>>> =
            BehaviorSubject.createDefault(Option.none())
    private val errorOnceAndStream: Subject<Option<Throwable>> =
            BehaviorSubject.createDefault(Option.none())

    override fun querySearch(query: String,
                             preliminaryTask: Completable): Completable {
        return preliminaryTask.doOnSubscribe { reportInProgress() } //   .doFinally(this::reportNotInProgress)
                .andThen(freeSoundApiService.search(Preconditions.get(query))
                        .map { it.results }
                        .doOnSuccess { reportResults(it) }
                        .doOnError { reportError(it) }
                        .ignoreElement()
                        .onErrorComplete())
    }

    override fun getSearchStateOnceAndStream(): Observable<SearchState> {
        return Observable.combineLatest(resultsOnceAndStream,
                        errorOnceAndStream,
                        inProgressOnceAndStream,
                        Function3 { results: Option<List<Sound>>,
                                    error: Option<Throwable>,
                                    inProgress: Boolean ->
                            combine(results, error, inProgress)
                        })
                .observeOn(schedulerProvider.computation())
                .distinctUntilChanged()
    }

    override fun clear(): Completable {
        return Completable.fromAction { reportClear() }
    }

    private fun reportClear() {
        resultsOnceAndStream.onNext(Option.none())
        errorOnceAndStream.onNext(Option.none())
        inProgressOnceAndStream.onNext(false)
    }

    private fun reportInProgress() {
        inProgressOnceAndStream.onNext(true)
    }

    private fun reportResults(results: List<Sound>) {
        resultsOnceAndStream.onNext(Option.ofObj(results))
        errorOnceAndStream.onNext(Option.none())
        inProgressOnceAndStream.onNext(false)
    }

    private fun reportError(e: Throwable) {
        errorOnceAndStream.onNext(Option.ofObj(e))
        inProgressOnceAndStream.onNext(false)
    }

    companion object {
        private fun combine(results: Option<List<Sound>>,
                            error: Option<Throwable>,
                            inProgress: Boolean): SearchState {
            // FIXME This is not an ideal implementation, but:
            //  1. This ViewModel will be re-implemented as MVI
            //  2. I really want to get rid of AutoValue and this is the last usage of it.
            if (error.isSome) {
                // No combining errors and existing state, sorry.
                return SearchState.Error(OptionUnsafe.getUnsafe(error))
            }
            if (inProgress) {
                // Progress can exist with results
                return SearchState.InProgress(results.orDefault { null })
            }
            return if (results.isSome) {
                // No error and not in progress, but have results -> success!
                SearchState.Success(OptionUnsafe.getUnsafe(results))
            } else SearchState.Cleared

            // Nothingness.
        }

    }

}