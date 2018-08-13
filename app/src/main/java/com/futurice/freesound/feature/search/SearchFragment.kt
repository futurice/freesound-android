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

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.futurice.freesound.R
import com.futurice.freesound.common.rx.plusAssign
import com.futurice.freesound.core.BindingBaseFragment
import com.futurice.freesound.feature.common.DisplayableItem
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.feature.common.ui.adapter.RecyclerViewAdapter
import com.futurice.freesound.inject.fragment.BaseFragmentModule
import com.futurice.freesound.network.api.model.Sound
import com.futurice.freesound.viewmodel.DataBinder
import com.futurice.freesound.viewmodel.ViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : BindingBaseFragment<SearchFragmentComponent>() {

    @Inject
    internal lateinit var searchFragmentViewModel: SearchFragmentViewModel

    @Inject
    internal lateinit var searchResultAdapter: RecyclerViewAdapter<Sound>

    @Inject
    internal lateinit var schedulerProvider: SchedulerProvider

    private val dataBinder = object : DataBinder {

        override fun bind(disposables: CompositeDisposable) {
            disposables += searchFragmentViewModel.soundsOnceAndStream
                    .subscribeOn(schedulerProvider.computation())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({ handleResults(it.orDefault { null }) })
                    { Timber.e(it, "Error setting Sound items") }
            disposables += searchFragmentViewModel.searchStateOnceAndStream
                    .subscribeOn(schedulerProvider.computation())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({ showProgress(it) })
                    { Timber.e(it, "Error receiving search triggered Events") }
        }

        override fun unbind() {
            searchFragmentViewModel.stopPlayback()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_searchResults.layoutManager = LinearLayoutManager(activity).apply {
            recycleChildrenOnDetach = true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView_searchResults.adapter = searchResultAdapter
    }

    override fun inject() {
        component().inject(this)
    }

    override fun createComponent(): SearchFragmentComponent =
            (activity as SearchActivity).component()
                    .plusSearchFragmentComponent(BaseFragmentModule(this))

    override fun viewModel(): ViewModel = searchFragmentViewModel

    override fun dataBinder(): DataBinder = dataBinder

    private fun handleResults(sounds: List<DisplayableItem<Sound>>?) {
        if (sounds != null) showResults(sounds) else showNothing()
    }

    private fun showNothing() {
        textView_searchNoResults.visibility = GONE
        recyclerView_searchResults.visibility = GONE
    }

    private fun showResults(sounds: List<DisplayableItem<Sound>>) {
        if (sounds.isEmpty()) {
            textView_searchNoResults.visibility = VISIBLE
            recyclerView_searchResults.visibility = GONE
        } else {
            textView_searchNoResults.visibility = GONE
            recyclerView_searchResults.visibility = VISIBLE
            searchResultAdapter.update(sounds)
        }
    }

    private fun showProgress(searchState: SearchState) {
        progressBar_searchProgress.visibility = if (searchState.isInProgress) VISIBLE else GONE
    }

    companion object {
        internal fun create(): SearchFragment = SearchFragment()
    }

}
