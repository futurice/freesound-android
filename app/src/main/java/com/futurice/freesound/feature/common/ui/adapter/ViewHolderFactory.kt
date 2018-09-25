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

package com.futurice.freesound.feature.common.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.ViewGroup

/**
 * Instantiates a [ViewHolder] based on the type.
 */
abstract class ViewHolderFactory protected constructor(protected val context: Context) {

    /**
     * Creates a [ViewHolder]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @return the newly created [ViewHolder]
     */
    abstract fun createViewHolder(parent: ViewGroup): ViewHolder
}
