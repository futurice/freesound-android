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

package com.futurice.freesound.feature.home.user

import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.mvi.BaseViewModel
import com.futurice.freesound.mvi.EventMapper
import com.futurice.freesound.mvi.Logger
import com.futurice.freesound.mvi.Store


// path builder
// types,
// initialEvent, eventMapper,
// store instance
//  - Action Transformer
//  - Default Value
//  - Reducer
// loggerTag, logger instance
internal class HomeFragmentViewModel(initialEvent: HomeUiEvent,
                                     eventMapper: EventMapper<HomeUiEvent, HomeUiAction>,
                                     store: Store<HomeUiAction, HomeUiResult, HomeUiModel>,
                                     schedulerProvider: SchedulerProvider,
                                     logTag: String,
                                     logger: Logger)
    : BaseViewModel<HomeUiEvent, HomeUiAction, HomeUiResult, HomeUiModel>(
        initialEvent = initialEvent,
        eventMapper = eventMapper,
        store = store,
        schedulerProvider = schedulerProvider,
        logTag = logTag,
        logger = logger)
