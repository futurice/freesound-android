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
import com.futurice.freesound.common.adapter.AdapterInteractor;
import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.network.api.model.Sound;
import com.squareup.picasso.Picasso;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import polanski.option.function.Func0;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class SoundItemAdapter extends RecyclerView.Adapter<SoundItemViewHolder> {

    @NonNull
    private final AdapterInteractor<DisplayableItem> adapterInteractor;

    @NonNull
    private final SoundItemViewModelFactory viewModelFactory;

    @NonNull
    private final Picasso picasso;

    SoundItemAdapter(@NonNull final AdapterInteractor<DisplayableItem> adapterInteractor,
                     @NonNull final Picasso picasso,
                     @NonNull final SoundItemViewModelFactory viewModelFactory) {
        this.adapterInteractor = get(adapterInteractor);
        this.viewModelFactory = get(viewModelFactory);
        this.picasso = get(picasso);
    }

    @Override
    public SoundItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.view_sound_item, parent, false);
        return new SoundItemViewHolder(view, picasso);
    }

    @Override
    public void onBindViewHolder(SoundItemViewHolder holder, int position) {
        adapterInteractor.getItem(position)
                         .ifSome(item -> holder
                                 .bind(viewModelFactory.create((Sound) item.model())));
    }

    @Override
    public void onViewRecycled(final SoundItemViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    /**
     * Appends new items to currently existing ones.
     *
     * @param items collection to append
     */
    void addItems(@NonNull final List<DisplayableItem> items) {
        applyChanges(() -> adapterInteractor.append(get(items)));
    }

    /**
     * Replaces items currently stored in the adapter with new items.
     *
     * @param items collection to update the previous values
     */
    void setItems(@NonNull final List<DisplayableItem> items) {
        applyChanges(() -> adapterInteractor.update(get(items)));
    }

    @Override
    public int getItemCount() {
        return adapterInteractor.getCount();
    }

    private void applyChanges(@NonNull final Func0<Boolean> applyFunction) {
        if (applyFunction.call()) {
            notifyDataSetChanged();
        }
    }
}
