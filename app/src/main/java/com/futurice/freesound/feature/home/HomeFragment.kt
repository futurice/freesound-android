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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futurice.freesound.R
import com.futurice.freesound.common.rx.plusAssign
import com.futurice.freesound.core.BindingBaseFragment
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider
import com.futurice.freesound.feature.images.circular
import com.futurice.freesound.inject.fragment.BaseFragmentModule
import com.futurice.freesound.viewmodel.DataBinder
import com.futurice.freesound.viewmodel.ViewModel
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BindingBaseFragment<HomeFragmentComponent>() {

    @Inject
    internal lateinit var homeFragmentViewModel: HomeFragmentViewModel

    @Inject
    internal lateinit var picasso: Picasso

    @Inject
    internal lateinit var schedulerProvider: SchedulerProvider

    private val dataBinder = object : DataBinder {

        override fun bind(d: CompositeDisposable) {
            d += homeFragmentViewModel.image
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe({
                               picasso.load(it)
                                   .transform(circular())
                                   .into(avatar_image)
                           })
                { e -> Timber.e(e, "Error setting image") }

            d += homeFragmentViewModel.userName
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe({ username_textView.text = it })
                { e -> Timber.e(e, "Error setting user") }

            d += homeFragmentViewModel.about
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe({ about_textView.text = it })
                { e -> Timber.e(e, "Error setting aboutTextView") }
        }

        override fun unbind() {
            picasso.cancelRequest(avatar_image)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater?.inflate(R.layout.fragment_home, container, false)

    override fun inject() {
        component().inject(this)
    }

    override fun createComponent(): HomeFragmentComponent =
        (activity as HomeActivity).component()
            .plus(BaseFragmentModule(this))

    override fun viewModel(): ViewModel =
        homeFragmentViewModel

    override fun dataBinder(): DataBinder =
        dataBinder

    companion object {

        internal fun create(): HomeFragment {
            return HomeFragment()
        }
    }

}
