package com.futurice.freesound.feature.search;

import com.futurice.freesound.R;
import com.futurice.freesound.viewmodel.Binder;
import com.futurice.freesound.viewmodel.viewholder.BaseBindingViewHolder;
import com.squareup.picasso.Picasso;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.util.Log.e;
import static com.futurice.freesound.utils.Preconditions.get;

class SoundItemViewHolder extends BaseBindingViewHolder<SoundItemViewModel> {

    private static final String TAG = SoundItemViewHolder.class.getSimpleName();

    private final View rootView;
    private final TextView titleTextView;
    private final TextView descriptionTextView;
    private final ImageView thumbnailImageView;
    private final Picasso picasso;

    private final Binder viewBinder = new Binder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscriptions) {

            final SoundItemViewModel vm = get(getViewModel());

            subscriptions.add(vm.name()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(titleTextView::setText,
                                           error -> e(TAG,
                                                      "Unable to set SoundItem name",
                                                      error)));
            subscriptions.add(vm.description()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(descriptionTextView::setText,
                                           error -> e(TAG,
                                                      "Unable to set SoundItem description",
                                                      error)));

            subscriptions.add(vm.thumbnailImageUrl()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(url -> picasso.load(url)
                                                         .into(thumbnailImageView),
                                           error -> e(TAG,
                                                      "Unable to set SoundItem thumbnail",
                                                      error)));

            rootView.setOnClickListener(__ -> vm.openDetails());
        }

        @Override
        public void unbind() {
            rootView.setOnClickListener(null);
            picasso.cancelRequest(thumbnailImageView);
        }

    };

    SoundItemViewHolder(@NonNull final View view,
                        @NonNull final Picasso picasso) {
        super(get(view));
        this.rootView = view;
        this.picasso = get(picasso);
        this.titleTextView = get((TextView) view.findViewById(R.id.textView_title));
        this.descriptionTextView = get((TextView) view.findViewById(R.id.textView_description));
        this.thumbnailImageView = get((ImageView) view.findViewById(R.id.imageView_soundItem));
    }

    @NonNull
    @Override
    protected Binder getViewBinder() {
        return viewBinder;
    }

}
