package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.common.utils.AndroidPreconditions;
import com.futurice.freesound.common.utils.Preconditions;
import com.futurice.freesound.feature.common.DisplayableItem;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

import static com.futurice.freesound.common.utils.Preconditions.get;

/**
 * Implementation of {@link RecyclerView.Adapter} for {@link DisplayableItem}.
 */
public final class RecyclerViewAdapter extends RecyclerView.Adapter {

    @NonNull
    private final List<DisplayableItem> modelItems = new ArrayList<>();

    @NonNull
    private final ItemComparator comparator;

    @NonNull
    private final Map<Integer, ViewHolderFactory> factoryMap;

    @NonNull
    private final Map<Integer, ViewHolderBinder> binderMap;

    public RecyclerViewAdapter(@NonNull final ItemComparator comparator,
                               @NonNull final Map<Integer, ViewHolderFactory> factoryMap,
                               @NonNull final Map<Integer, ViewHolderBinder> binderMap) {
        this.comparator = comparator;
        this.factoryMap = factoryMap;
        this.binderMap = binderMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return factoryMap.get(viewType).createViewHolder(get(parent));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final DisplayableItem item = modelItems.get(position);
        binderMap.get(item.type()).bind(get(holder), item);
    }

    @Override
    public void onViewRecycled(final RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ViewHolderBinder.unbind(holder);
    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return modelItems.get(position).type();
    }

    /**
     * Updates modelItems currently stored in adapter with the new modelItems.
     *
     * @param items collection to update the previous values
     */
    public void update(@NonNull final List<DisplayableItem> items) {
        Preconditions.checkNotNull(items);
        AndroidPreconditions.assertUiThread();

        if (modelItems.isEmpty()) {
            updateAllItems(items);
        } else {
            updateDiffItemsOnly(items);
        }
    }

    /**
     * Only use for the first update of the adapter, whe it is still empty.
     */
    private void updateAllItems(@NonNull final List<DisplayableItem> items) {
        Observable.just(items)
                  .doOnNext(this::updateItemsInModel)
                  .subscribe(__ -> notifyDataSetChanged());
    }

    /**
     * Do not use for first update of the adapter.
     * The method {@link DiffUtil.DiffResult#dispatchUpdatesTo(RecyclerView.Adapter)} is
     * significantly slower than {@link RecyclerViewAdapter#notifyDataSetChanged()} when it comes
     * to update all the items in the adapter.
     */
    private void updateDiffItemsOnly(@NonNull final List<DisplayableItem> items) {
        Observable.fromCallable(() -> calculateDiff(items))
                  .doOnNext(__ -> updateItemsInModel(items))
                  .subscribe(this::updateAdapterWithDiffResult);
    }

    private DiffUtil.DiffResult calculateDiff(@NonNull final List<DisplayableItem> newItems) {
        return DiffUtil.calculateDiff(new DiffUtilCallback(modelItems, newItems, comparator));
    }

    private void updateItemsInModel(@NonNull final List<DisplayableItem> items) {
        modelItems.clear();
        modelItems.addAll(items);
    }

    private void updateAdapterWithDiffResult(@NonNull final DiffUtil.DiffResult result) {
        result.dispatchUpdatesTo(this);
    }
}
