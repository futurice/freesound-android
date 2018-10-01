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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import butterknife.ButterKnife.findById
import com.futurice.freesound.R
import com.futurice.freesound.app.FreesoundApplication
import com.futurice.freesound.core.BindingBaseActivity
import com.futurice.freesound.feature.home.user.HomeFragment
import com.futurice.freesound.inject.activity.BaseActivityModule
import com.futurice.freesound.arch.viewmodel.DataBinder
import com.futurice.freesound.arch.viewmodel.SimpleDataBinder
import com.futurice.freesound.arch.viewmodel.ViewModel
import javax.inject.Inject

class HomeActivity : BindingBaseActivity<HomeActivityComponent>() {

    @Inject
    internal lateinit var homeViewModel: HomeViewModel

    private val dataBinder = SimpleDataBinder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState == null) {
            addHomeFragment()
        }

        setSupportActionBar(findById(this, R.id.toolbar_home))
    }

    override fun viewModel(): ViewModel = homeViewModel

    override fun dataBinder(): DataBinder = dataBinder

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> true
            R.id.action_search -> {
                homeViewModel.openSearch()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun createComponent(): HomeActivityComponent {
        return (application as FreesoundApplication).component()
                .plusHomeActivityComponent(
                        BaseActivityModule(this))
    }

    override fun inject() {
        component().inject(this)
    }

    private fun addHomeFragment() {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, HomeFragment.create())
                .commit()
    }
}
