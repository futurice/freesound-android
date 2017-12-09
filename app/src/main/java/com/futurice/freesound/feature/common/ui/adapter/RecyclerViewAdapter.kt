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

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.futurice.freesound.common.utils.Preconditions.get
import com.futurice.freesound.feature.common.DisplayableItem
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.viewmodel.viewholder.BaseBindingViewHolder
import java.util.*

/**
 * Implementation of [RecyclerView.Adapter] for [DisplayableItem].
 */
class RecyclerViewAdapter<T>(private val comparator: ItemComparator,
                             private val factoryMap: Map<Int, ViewHolderFactory>,
                             private val binderMap: Map<Int, ViewHolderBinder<T>>,
                             private val schedulerProvider: SchedulerProvider) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val modelItems = ArrayList<DisplayableItem<T>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            factoryMap[viewType]?.createViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = modelItems[position]
        binderMap[item.type]?.bind(holder, item)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder?) {
        super.onViewRecycled(holder)
        (holder as? BaseBindingViewHolder<*>)?.unbind()
    }

    override fun getItemCount() = modelItems.size

    override fun getItemViewType(position: Int) = modelItems[position].type

    /**
     * Updates modelItems currently stored in adapter with the new modelItems.
     *
     * @param items collection to update the previous values
     */
    fun update(items: List<DisplayableItem<T>>) {
        schedulerProvider.assertUiThread()

        if (modelItems.isEmpty()) {
            updateAllItems(items)
        } else {
            updateDiffItemsOnly(items)
        }
    }

    /**
     * Only used for the first update of the adapter, when it is still empty.
     */
    private fun updateAllItems(items: List<DisplayableItem<T>>) {
        updateItemsInModel(items)
        notifyDataSetChanged()
    }

    /**
     * Only used after the first update of the adapter.
     * The method [DiffUtil.DiffResult.dispatchUpdatesTo] is
     * significantly slower than [RecyclerViewAdapter.notifyDataSetChanged] when it comes
     * to update all the items in the adapter.
     */
    private fun updateDiffItemsOnly(items: List<DisplayableItem<T>>) {
        val itemsCopy = ArrayList(items)
        updateItemsInModel(items)
        val diff = calculateDiff(itemsCopy)
        updateAdapterWithDiffResult(diff)
    }

    private fun calculateDiff(newItems: List<DisplayableItem<T>>) =
            DiffUtil.calculateDiff(DiffUtilCallback(modelItems, newItems, comparator))

    private fun updateItemsInModel(items: List<DisplayableItem<T>>) {
        modelItems.apply {
            clear()
            addAll(items)
        }
    }

    private fun updateAdapterWithDiffResult(result: DiffUtil.DiffResult) {
        result.dispatchUpdatesTo(this)
    }
}
