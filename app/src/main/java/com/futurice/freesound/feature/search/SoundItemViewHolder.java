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
import com.futurice.freesound.feature.common.waveform.BlackBackgroundWaveformExtractor;
import com.futurice.freesound.feature.common.waveform.PlaybackWaveformView;
import com.futurice.freesound.feature.common.waveform.WaveformViewTarget;
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

import static butterknife.ButterKnife.findById;
import static com.futurice.freesound.utils.Preconditions.get;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

class SoundItemViewHolder extends BaseBindingViewHolder<SoundItemViewModel> {

    @BindView(R.id.textView_title)
    TextView titleTextView;

    @BindView(R.id.textView_description)
    TextView descriptionTextView;

    @BindView(R.id.playbackWaveformView_soundItem)
    PlaybackWaveformView playbackWaveformView;

    @NonNull
    private final Picasso picasso;

    @NonNull
    private final Target playbackWaveformViewTarget;

    @NonNull
    private final DataBinder viewDataBinder = new DataBinder() {

        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            final SoundItemViewModel vm = get(getViewModel());

            // Synchronously clear the waveform, it might be recycled.
            playbackWaveformView.clearWaveform();

            disposables.add(vm.name()
                              .observeOn(mainThread())
                              .subscribe(titleTextView::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem name")));
            disposables.add(vm.description()
                              .observeOn(mainThread())
                              .subscribe(descriptionTextView::setText,
                                         e -> Timber
                                                 .e(e, "Unable to set SoundItem description")));

            disposables.add(vm.duration()
                              .observeOn(mainThread())
                              .subscribe(
                                      duration -> playbackWaveformView
                                              .setMetadata(duration),
                                      e -> Timber.e(e, "Unable to set SoundItem thumbnail")));

            disposables.add(vm.thumbnailImageUrl()
                              .observeOn(mainThread())
                              .subscribe(url -> picasso.load(url)
                                                       .into(playbackWaveformViewTarget),
                                         e -> Timber.e(e, "Unable to set SoundItem thumbnail")));

            playbackWaveformView.setOnClickListener(__ -> vm.toggleSoundPlayback());
        }

        @Override
        public void unbind() {
            playbackWaveformView.setOnClickListener(null);
            picasso.cancelRequest(playbackWaveformViewTarget);
        }

    };

    SoundItemViewHolder(@NonNull final View view,
                        @NonNull final Picasso picasso) {
        super(get(view));
        ButterKnife.bind(this, view);
        this.picasso = get(picasso);
        this.playbackWaveformViewTarget = new WaveformViewTarget(
                findById(view, R.id.playbackWaveformView_soundItem),
                new BlackBackgroundWaveformExtractor());
    }

    @NonNull
    protected DataBinder getViewDataBinder() {
        return viewDataBinder;
    }

}
