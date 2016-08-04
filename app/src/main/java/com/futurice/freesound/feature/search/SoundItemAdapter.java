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
import com.futurice.freesound.network.api.model.Sound;
import com.squareup.picasso.Picasso;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.futurice.freesound.utils.Preconditions.get;

final class SoundItemAdapter extends RecyclerView.Adapter<SoundItemViewHolder> {

    @NonNull
    private final List<Sound> items;

    @NonNull
    private final SoundItemViewModel_Factory viewModelFactory;

    @NonNull
    private final Picasso picasso;

    SoundItemAdapter(@NonNull final Picasso picasso,
                     @NonNull final SoundItemViewModel_Factory viewModelFactory) {
        this(new ArrayList<>(), picasso, viewModelFactory);
    }

    private SoundItemAdapter(@NonNull final List<Sound> initialItems,
                             @NonNull final Picasso picasso,
                             @NonNull final SoundItemViewModel_Factory viewModelFactory) {
        this.items = get(initialItems);
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
        holder.bind(viewModelFactory.create(items.get(position)));
    }

    @Override
    public void onViewRecycled(final SoundItemViewHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    void addItems(@NonNull final List<Sound> items) {
        this.items.addAll(get(items));
        notifyDataSetChanged();
    }

    void setItems(@NonNull final List<Sound> items) {
        this.items.clear();
        addItems(get(items));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
