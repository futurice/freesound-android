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

package com.futurice.freesound.feature.home.user

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futurice.freesound.R
import com.futurice.freesound.arch.mvi.view.MviBaseFragment
import com.futurice.freesound.feature.home.HomeActivity
import com.futurice.freesound.feature.images.circularTransformation
import com.futurice.freesound.inject.fragment.BaseFragmentModule
import com.jakewharton.rxbinding2.support.design.widget.dismisses
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.squareup.picasso.Picasso
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

/**
 * The Snackbar is particularly annoying because when the Layout changes, the Snackbar is automatically
 * dismissed, which occurs AFTER the repeated error has been applied; so the information is lost.
 * I've worked around this by not using INDEFINITE.
 */
class HomeFragment : MviBaseFragment<HomeFragmentComponent, HomeUiModel, HomeUiEvent>() {

    @Inject
    internal lateinit var picasso: Picasso

    internal lateinit var errorSnackBar: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
                ?.also { errorSnackBar = createSnackbar(it) }
    }

    private fun createSnackbar(view: View): Snackbar {
        return Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_LONG)
                .apply {
                    setAction(android.R.string.ok, { dismiss() })
                    setActionTextColor(ContextCompat.getColor(view.context, R.color.colorContrastAccent))
                }
    }

    override fun inject() = component().inject(this)

    override fun createComponent(): HomeFragmentComponent {
        return (activity as HomeActivity)
                .component().plus(HomeFragmentModule(this),
                        BaseFragmentModule(this))
    }

    override fun uiEvents(): LiveData<HomeUiEvent> {
        return LiveDataReactiveStreams.fromPublisher(
                Flowable.merge(errorIndicatorDismissed(), refreshRequested()))
    }

    override fun render(model: HomeUiModel) {

        val (user, isLoading, isRefreshing, errorMsg) = model

        when (user) {
            null -> hideUser()
            else -> showUser(user)
        }

        showLoading(isLoading)
        showRefreshing(isRefreshing)

        when (errorMsg) {
            null -> errorSnackBar.dismiss()
            else -> errorSnackBar.setText(errorMsg).show()
        }

    }

    private fun errorIndicatorDismissed() = errorSnackBar.dismisses()
            .map { HomeUiEvent.ErrorIndicatorDismissed }
            .toFlowable(BackpressureStrategy.BUFFER)

    private fun refreshRequested() = feed_swipeToRefresh.refreshes()
            .map { HomeUiEvent.RefreshRequested }
            .toFlowable(BackpressureStrategy.BUFFER)

    override fun cancel() = picasso.cancelRequest(avatar_image)

    private fun hideUser() {
        homeUser_container.visibility = View.GONE
    }

    private fun showUser(user: UserUiModel) {
        homeUser_container.visibility = View.VISIBLE
        picasso.load(user.avatarUrl)
                .transform(circularTransformation())
                .into(avatar_image)

        username_textView.text = user.username
        about_textView.text = user.about
    }

    private fun showLoading(isLoading: Boolean) {
        loading_progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showRefreshing(isRefreshing: Boolean) {
        feed_swipeToRefresh.isRefreshing = isRefreshing
    }

    companion object {
        fun create(): HomeFragment {
            return HomeFragment()
        }
    }
}
