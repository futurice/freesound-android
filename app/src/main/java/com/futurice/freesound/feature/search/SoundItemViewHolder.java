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

package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.feature.common.BlackBackgroundWaveformExtractor;
import com.futurice.freesound.feature.common.view.WaveformView;
import com.futurice.freesound.feature.common.view.WaveformViewTarget;
import com.futurice.freesound.viewmodel.DataBinder;
import com.futurice.freesound.viewmodel.viewholder.BaseBindingViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

class SoundItemViewHolder extends BaseBindingViewHolder<SoundItemViewModel> {

    @BindView(R.id.textView_title)
    TextView titleTextView;

    @BindView(R.id.textView_description)
    TextView descriptionTextView;

    @BindView(R.id.waveformView_soundItem)
    WaveformView waveFormView;

    @NonNull
    private final Target waveformViewTarget;

    @NonNull
    private final Picasso picasso;

    @NonNull
    private final DataBinder viewDataBinder = new DataBinder() {

        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            final SoundItemViewModel vm = get(getViewModel());

            disposables.add(vm.name()
                              .observeOn(mainThread())
                              .subscribe(get(titleTextView)::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem name")));
            disposables.add(vm.description()
                              .observeOn(mainThread())
                              .subscribe(get(descriptionTextView)::setText,
                                         e -> Timber
                                                 .e(e, "Unable to set SoundItem description")));

            disposables.add(vm.thumbnailImageUrl()
                              .observeOn(mainThread())
                              .subscribe(url -> picasso.load(url)
                                                       .into(waveformViewTarget),
                                         e -> Timber.e(e, "Unable to set SoundItem thumbnail")));

            waveFormView.setOnClickListener(__ -> vm.togglePreviewPlayback());
        }

        @Override
        public void unbind() {
            waveFormView.setOnClickListener(null);
            picasso.cancelRequest(waveformViewTarget);
            get(getViewModel()).stopPreviewPlayback();
        }

    };

    SoundItemViewHolder(@NonNull final View view,
                        @NonNull final Picasso picasso) {
        super(get(view));
        ButterKnife.bind(this, view);
        this.picasso = get(picasso);
        this.waveformViewTarget = new WaveformViewTarget(waveFormView,
                                                         new BlackBackgroundWaveformExtractor());
    }

    @NonNull
    protected DataBinder getViewDataBinder() {
        return viewDataBinder;
    }

}
