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

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.futurice.freesound.R
import com.futurice.freesound.feature.images.circularTransformation
import com.futurice.freesound.inject.fragment.BaseFragmentModule
import com.futurice.freesound.mvi.BaseMviFragment
import com.jakewharton.rxbinding2.support.design.widget.dismisses
import com.squareup.picasso.Picasso
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseMviFragment<HomeFragmentComponent, HomeFragmentUiModel, HomeFragmentUiEvent>() {

    @Inject
    internal lateinit var picasso: Picasso

    internal lateinit var errorSnackBar: Snackbar

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_home, container, false)
                ?.also { errorSnackBar = createSnackbar(it) }
    }

    private fun createSnackbar(view: View): Snackbar {
        return Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_INDEFINITE)
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

    override fun uiEvents(): LiveData<HomeFragmentUiEvent> {
        return LiveDataReactiveStreams.fromPublisher(
                Flowable.merge(errorIndicatorDismissed(), refreshRequested()))
    }

    override fun render(model: HomeFragmentUiModel) {

        when (model.user) {
            null -> hideUser()
            else -> showUser(model.user)
        }

        showLoading(model.isLoading)

        when (model.errorMsg) {
            null -> errorSnackBar.dismiss()
            else -> errorSnackBar.setText(model.errorMsg).show()
        }

    }

    // TODO Make data and ui flowable extensions.

    private fun errorIndicatorDismissed() = errorSnackBar.dismisses()
            .map { HomeFragmentUiEvent.ErrorIndicatorDismissed }
            .toFlowable(BackpressureStrategy.BUFFER)

    private fun refreshRequested() = Flowable.never<HomeFragmentUiEvent.ContentRefreshRequested>()

    // TODO This is currently never called - there's no lifecycle hook.
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun cancel() = picasso.cancelRequest(avatar_image)

    private fun hideUser() {
        homeUser_container.visibility = View.GONE
    }

    private fun showUser(user: Fragment.UserUiModel) {
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

    companion object {
        fun create(): HomeFragment {
            return HomeFragment()
        }
    }
}
