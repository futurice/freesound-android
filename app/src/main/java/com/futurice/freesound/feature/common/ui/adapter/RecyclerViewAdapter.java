package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.common.utils.AndroidPreconditions;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.inject.fragment.FragmentScope;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation of {@link android.support.v7.widget.RecyclerView.Adapter} for {@link
 * DisplayableItem}.
 */
@FragmentScope
public final class RecyclerViewAdapter extends RecyclerView.Adapter {

    @NonNull
    private final List<DisplayableItem> mItems = new ArrayList<>();

    @NonNull
    private final IListItemComparator mComparator;

    @NonNull
    private final IViewHolderFactory mInstantiator;

    @NonNull
    private final IViewHolderBinder<DisplayableItem> mBinder;

    private Executor mExecutor = Executors.newSingleThreadExecutor();

    @Inject
    RecyclerViewAdapter(@NonNull final IListItemComparator comparator,
                        @NonNull final IViewHolderFactory instantiator,
                        @NonNull final IViewHolderBinder<DisplayableItem> binder) {
        mComparator = comparator;
        mInstantiator = instantiator;
        mBinder = binder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return mInstantiator.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        mBinder.bind(holder, mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return mItems.get(position).type().ordinal();
    }

    /**
     * Updates mItems currently stored in adapter with the new mItems.
     *
     * @param items collection to update the previous values
     */
    public void update(@NonNull final List<DisplayableItem> items) {
        Observable.fromCallable(() -> calculateDiff(items))
                  .doOnNext(__ -> updateItems(items))
                  .subscribeOn(Schedulers.from(mExecutor))
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(this::updateAdapterWithDiffResult);
    }

    private DiffUtil.DiffResult calculateDiff(@NonNull final List<DisplayableItem> newItems) {
        AndroidPreconditions.assertWorkerThread();

        return DiffUtil.calculateDiff(new DiffUtilCallback(mItems, newItems, mComparator));
    }

    private void updateItems(@NonNull final List<DisplayableItem> items) {
        AndroidPreconditions.assertWorkerThread();

        mItems.clear();
        mItems.addAll(items);
    }

    private void updateAdapterWithDiffResult(@NonNull final DiffUtil.DiffResult result) {
        AndroidPreconditions.assertUiThread();

        result.dispatchUpdatesTo(this);
    }
}
