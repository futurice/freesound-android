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
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.feature.common.ui.adapter.ViewHolderBinder;
import com.futurice.freesound.feature.common.ui.adapter.ViewHolderFactory;
import com.futurice.freesound.feature.common.waveform.BlackBackgroundWaveformExtractor;
import com.futurice.freesound.feature.common.waveform.PlaybackWaveformView;
import com.futurice.freesound.feature.common.waveform.WaveformViewTarget;
import com.futurice.freesound.feature.images.PicassoTransformations;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.arch.viewmodel.DataBinder;
import com.futurice.freesound.arch.viewmodel.viewholder.BaseBindingViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;
import static com.futurice.freesound.common.utils.Preconditions.get;

final class SoundItemViewHolder extends BaseBindingViewHolder<SoundItemViewModel> {

    @BindView(R.id.imageView_avatar)
    ImageView avatarImageView;

    @BindView(R.id.textView_username)
    TextView usernameTextView;

    @BindView(R.id.textView_date)
    TextView dateTextView;

    @BindView(R.id.textView_title)
    TextView titleTextView;

    @BindView(R.id.textView_description)
    TextView descriptionTextView;

    @BindView(R.id.playbackWaveformView_soundItem)
    PlaybackWaveformView playbackWaveformView;

    @NonNull
    private final Picasso picasso;

    @NonNull
    private final SchedulerProvider schedulerProvider;

    @NonNull
    private final Target playbackWaveformViewTarget;

    @NonNull
    private final DataBinder viewDataBinder = new DataBinder() {

        @Override
        public void bind(@NonNull final CompositeDisposable disposables) {
            final SoundItemViewModel vm = get(getViewModel());

            // Synchronously clear the waveform, it might be recycled.
            playbackWaveformView.clearWaveform();

            disposables.add(vm.userAvatar()
                              .subscribeOn(schedulerProvider.computation())
                              .observeOn(schedulerProvider.ui())
                              .subscribe(url -> picasso.load(url)
                                                       .transform(PicassoTransformations
                                                                          .circularTransformation())
                                                       .into(avatarImageView),
                                         e -> Timber.e(e, "Unable to set SoundItem avatar")));

            disposables.add(vm.username()
                              .subscribe(usernameTextView::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem username")));

            disposables.add(vm.createdDate()
                              .subscribe(dateTextView::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem created date")));

            disposables.add(vm.name()
                              .observeOn(schedulerProvider.ui())
                              .subscribe(titleTextView::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem name")));

            disposables.add(vm.description()
                              .observeOn(schedulerProvider.ui())
                              .subscribe(descriptionTextView::setText,
                                         e -> Timber.e(e, "Unable to set SoundItem description")));

            disposables.add(vm.duration()
                              .observeOn(schedulerProvider.ui())
                              .subscribe(playbackWaveformView::setMetadata,
                                         e -> Timber.e(e, "Unable to set SoundItem duration")));

            disposables.add(vm.thumbnailImageUrl()
                              .observeOn(schedulerProvider.ui())
                              .subscribe(url -> picasso.load(url)
                                                       .into(playbackWaveformViewTarget),
                                         e -> Timber.e(e, "Unable to set SoundItem thumbnail")));

            disposables.add(vm.progressPercentage()
                              .observeOn(schedulerProvider.ui())
                              .subscribe(playbackWaveformView::setProgress,
                                         e -> Timber.e(e, "Unable to set SoundItem progress")));

            playbackWaveformView.setOnClickListener(__ -> vm.toggleSoundPlayback());
        }

        @Override
        public void unbind() {
            playbackWaveformView.setOnClickListener(null);
            picasso.cancelRequest(playbackWaveformViewTarget);
            picasso.cancelRequest(avatarImageView);
            avatarImageView.setImageResource(R.drawable.avatar_placeholder);
        }

    };

    private SoundItemViewHolder(@NonNull final View view,
                                @NonNull final Picasso picasso,
                                @NonNull final SchedulerProvider schedulerProvider) {
        super(get(view));
        ButterKnife.bind(this, view);
        this.picasso = get(picasso);
        this.playbackWaveformViewTarget = new WaveformViewTarget(
                findById(view, R.id.playbackWaveformView_soundItem),
                new BlackBackgroundWaveformExtractor());
        this.schedulerProvider = get(schedulerProvider);
    }

    @NonNull
    protected DataBinder getViewDataBinder() {
        return viewDataBinder;
    }

    static class SoundItemViewHolderFactory extends ViewHolderFactory {

        @NonNull
        private final Picasso picasso;

        @NonNull
        private final SchedulerProvider schedulerProvider;

        SoundItemViewHolderFactory(@NonNull final Context context,
                                   @NonNull final Picasso picasso,
                                   @NonNull final SchedulerProvider schedulerProvider) {
            super(context);
            this.picasso = picasso;
            this.schedulerProvider = schedulerProvider;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder createViewHolder(@NonNull final ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.view_sound_item, parent, false);
            return new SoundItemViewHolder(view, picasso, schedulerProvider);
        }
    }

    static class SoundItemViewHolderBinder implements ViewHolderBinder<Sound> {

        @NonNull
        private final SoundItemViewModelFactory viewModelFactory;

        SoundItemViewHolderBinder(@NonNull final SoundItemViewModelFactory viewModelFactory) {
            this.viewModelFactory = viewModelFactory;
        }

        @Override
        public void bind(@NonNull final RecyclerView.ViewHolder viewHolder,
                         @NonNull final DisplayableItem<Sound> item) {
            SoundItemViewHolder soundItemViewHolder = SoundItemViewHolder.class.cast(viewHolder);
            Sound sound = Sound.class.cast(item.getModel());
            soundItemViewHolder.bind(viewModelFactory.create(sound));
        }
    }
}
