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

package com.futurice.freesound.feature.common.ui.adapter;

import com.futurice.freesound.feature.common.DisplayableItem;
import com.futurice.freesound.feature.common.scheduling.SchedulerProvider;
import com.futurice.freesound.test.rx.TrampolineSchedulerProvider;
import com.futurice.freesound.viewmodel.viewholder.BaseBindingViewHolder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by Futurice
 */
public class RecyclerViewAdapterTest {

    @Mock
    private ItemComparator comparator;

    @Mock
    private ViewHolderFactory factory1;

    @Mock
    private ViewHolderFactory factory2;

    @Mock
    private ViewHolderBinder binder1;

    @Mock
    private ViewHolderBinder binder2;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private RecyclerViewAdapter adapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SchedulerProvider schedulerProvider = new TrampolineSchedulerProvider();
        adapter = new RecyclerViewAdapter(comparator, factoryMap(), binderMap(), schedulerProvider);
    }

    @Test
    public void expectedNPEWhenViewGroupIsNullInOnCreateViewHolder() {
        thrown.expect(NullPointerException.class);

        adapter.onCreateViewHolder(null, 1);
    }

    @Test
    public void expectedNPEWhenTypeDoesNotExistInOnCreateViewHolder() {
        thrown.expect(NullPointerException.class);

        adapter.onCreateViewHolder(null, 5);
    }

    @Test
    public void factoryForPassedTypeCreatesViewHolder() {
        ViewGroup viewGroup = mock(ViewGroup.class);

        adapter.onCreateViewHolder(viewGroup, 1);

        Mockito.verify(factory1).createViewHolder(viewGroup);
    }

    @Test
    public void expectedNPEWhenNullIsPassedToUpdate() {
        thrown.expect(NullPointerException.class);

        adapter.update(null);
    }

    @Test
    public void diffUtilsIsNotUsedWhenUpdatingAdapterForTheFirstTime() {
        List<DisplayableItem> itemList = itemList();

        // FIXME: AndroidPreconditions.assertUiThread(), how to mock this ?
        adapter.update(itemList);

        verify(comparator, never()).areItemsTheSame(any(), any());
    }

    @Test
    public void diffUtilsIsUsedWhenUpdatingAdapterForTheFirstTime() {
        List<DisplayableItem> itemList = itemList();

        adapter.update(itemList);
        adapter.update(itemList);

        verify(comparator, atLeast(1)).areItemsTheSame(any(), any());
    }

    @Test
    public void expectedNPEWhenViewHolderIsNullInOnBindViewHolder() {
        thrown.expect(NullPointerException.class);
        List<DisplayableItem> itemList = itemList();

        adapter.update(itemList);
        adapter.onBindViewHolder(null, 1);
    }

    @Test
    public void expectedIndexOutOfBoundsExceptionWhenPositionIsOutOfBoundsInOnBindViewHolder() {
        thrown.expect(IndexOutOfBoundsException.class);
        List<DisplayableItem> itemList = itemList();
        RecyclerView.ViewHolder viewHolder = mock(RecyclerView.ViewHolder.class);

        adapter.update(itemList);
        adapter.onBindViewHolder(viewHolder, 5);
    }

    @Test
    public void binderForItemTypeBindsItemToViewHolder() {
        List<DisplayableItem> itemList = itemList();
        RecyclerView.ViewHolder viewHolder = mock(RecyclerView.ViewHolder.class);

        adapter.update(itemList);
        adapter.onBindViewHolder(viewHolder, 1);

        verify(binder2).bind(viewHolder, itemList.get(1));
    }

    @Test
    public void unbindIsCalledForBindingViewHolderInOnViewRecycled() {
        BaseBindingViewHolder viewHolder = mock(BaseBindingViewHolder.class);

        //FIXME: This throws NPE
        adapter.onViewRecycled(viewHolder);

        verify(viewHolder).unbind();
    }

    @Test
    public void itemCount() {
        List<DisplayableItem> itemList = itemList();

        adapter.update(itemList);

        assertThat(adapter.getItemCount()).isEqualTo(itemList.size());
    }

    @Test
    public void getItemViewType() {
        List<DisplayableItem> itemList = itemList();

        adapter.update(itemList);

        assertThat(adapter.getItemViewType(0)).isEqualTo(itemList.get(0).type());
        assertThat(adapter.getItemViewType(1)).isEqualTo(itemList.get(1).type());
        assertThat(adapter.getItemViewType(2)).isEqualTo(itemList.get(2).type());
    }

    private static List<DisplayableItem> itemList() {
        return new ArrayList<DisplayableItem>() {{
            add(DisplayableItem.create(new Object(), 1));
            add(DisplayableItem.create(new Object(), 2));
            add(DisplayableItem.create(new Object(), 1));
        }};
    }

    private Map<Integer, ViewHolderFactory> factoryMap() {
        return new HashMap<Integer, ViewHolderFactory>() {{
            put(1, factory1);
            put(2, factory2);
        }};
    }

    private Map<Integer, ViewHolderBinder> binderMap() {
        return new HashMap<Integer, ViewHolderBinder>() {{
            put(1, binder1);
            put(2, binder2);
        }};
    }

}
