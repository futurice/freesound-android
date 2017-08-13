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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.Toolbar
import android.view.View
import butterknife.ButterKnife.findById
import com.futurice.freesound.R
import com.futurice.freesound.app.FreesoundApplication
import com.futurice.freesound.common.rx.plusAssign
import com.futurice.freesound.common.utils.Preconditions.checkNotNull
import com.futurice.freesound.common.utils.Preconditions.get
import com.futurice.freesound.core.BindingBaseActivity
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.inject.activity.BaseActivityModule
import com.futurice.freesound.viewmodel.DataBinder
import com.futurice.freesound.viewmodel.SimpleDataBinder
import com.futurice.freesound.viewmodel.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_search.*
import polanski.option.Option
import timber.log.Timber.e
import javax.inject.Inject

class SearchActivity : BindingBaseActivity<SearchActivityComponent>() {

    @Inject
    internal lateinit var searchViewModel: SearchActivityViewModel

    @Inject
    internal lateinit var searchSnackbar: SearchSnackbar

    @Inject
    internal lateinit var schedulerProvider: SchedulerProvider

    private val dataBinder = object : SimpleDataBinder() {

        private fun SearchView.getTextChangeStream(uiScheduler: Scheduler): Observable<String> =
            Observable.create<String> { subscribeToSearchView(it) }
                .subscribeOn(uiScheduler)

        override fun bind(d: CompositeDisposable) {
            d += searchViewModel.isClearEnabledOnceAndStream
                .observeOn(schedulerProvider.ui())
                .subscribe({ this@SearchActivity.setClearSearchVisible(it) })
                { e(it, "Error setting query string") }

            d += search_view.getTextChangeStream(schedulerProvider.ui())
                .observeOn(schedulerProvider.computation())
                .subscribe({ searchViewModel.search(it) })
                { e(it, "Error getting changed text") }

            d += searchViewModel.searchStateOnceAndStream
                .observeOn(schedulerProvider.ui())
                .subscribe({ this@SearchActivity.handleErrorState(it) })
                { e(it, "Error receiving Errors") }
        }

        private fun SearchView.subscribeToSearchView(emitter: ObservableEmitter<String>) {
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(newText: String): Boolean {
                    emitter.onNext(get(newText))
                    return true
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        Option.ofObj(savedInstanceState)
            .ifNone({ this.addSearchFragment() })

        findById<Toolbar>(this, R.id.toolbar_search)
            .apply { setSupportActionBar(this) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search_view.apply {
            isIconified = false
            setOnCloseListener {
                search_view.setQuery(SearchActivityViewModel.NO_SEARCH, true)
                true
            }
        }
    }

    override fun viewModel(): ViewModel = searchViewModel

    override fun dataBinder(): DataBinder = dataBinder

    override fun inject() {
        component().inject(this)
    }

    override fun createComponent(): SearchActivityComponent =
        (application as FreesoundApplication).component()
            .plusSearchActivityComponent(BaseActivityModule(this))

    override fun onPause() {
        dismissSnackbar()
        super.onPause()
    }

    private fun addSearchFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.container, SearchFragment.create())
            .commit()
    }

    private fun handleErrorState(searchState: SearchState) {
        searchState.error()
            .ifSome { showSnackbar(getString(R.string.search_error)) }
            .ifNone({ this.dismissSnackbar() })
    }

    private fun setClearSearchVisible(isClearButtonVisible: Boolean) {
        val closeButton = search_view.findViewById(R.id.search_close_btn)
        closeButton.visibility = if (isClearButtonVisible) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(charSequence: CharSequence) {
        checkNotNull(charSequence)
        searchSnackbar.showNewSnackbar(search_coordinatorlayout, charSequence)
    }

    private fun dismissSnackbar() {
        searchSnackbar.dismissSnackbar()
    }

    companion object {

        fun open(context: Context) {
            Intent(context, SearchActivity::class.java)
                .apply { context.startActivity(this) }
        }
    }
}
