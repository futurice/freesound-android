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

package com.futurice.freesound.core;

import com.futurice.freesound.mvi.UiBinder;
import com.futurice.freesound.mvi.UiEventProducer;

import javax.inject.Inject;

/**
 * A base Fragment which provides the binding mechanism hooks to a MviView Model.
 *
 * @param <T> The DI component class.
 */
public abstract class BindingBaseFragment2<T, M, E> extends BaseFragment<T>
        implements UiEventProducer<E> {

    @Inject
    UiBinder<M, E> uiBinder;

}
