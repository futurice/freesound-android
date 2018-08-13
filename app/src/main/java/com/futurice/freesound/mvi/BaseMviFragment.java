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

package com.futurice.freesound.mvi;

import com.futurice.freesound.core.BaseFragment;

import javax.inject.Inject;

/**
 * A base Fragment which provides the binding mechanism hooks to a MviView Model.
 *
 * @param <C> The DI component class.
 */
public abstract class BaseMviFragment<C, M, E> extends BaseFragment<C> implements MviView<E, M> {

    @Inject
    UiBinder<M, E> uiBinder;

}
