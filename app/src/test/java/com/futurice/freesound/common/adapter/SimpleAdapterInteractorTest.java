/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.common.adapter;

import org.junit.Before;
import org.junit.Test;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static polanski.option.Option.NONE;
import static polanski.option.Option.ofObj;

public class SimpleAdapterInteractorTest {

    private SimpleAdapterInteractor<String> adapter;

    @Before
    public void setUp() {
        adapter = new SimpleAdapterInteractor<>();
    }

    @Test
    public void reset_emptiesTheAdapter_whenAdapterIsNotEmpty() {
        adapter.append(rangeStringCollection(0, 5));

        boolean reset = adapter.reset();

        assertThat(reset).isTrue();
        assertThat(adapter.getCount()).isZero();
    }

    @Test
    public void reset_returnsFalse_whenAdapterIsEmpty() {
        adapter.append(emptyStringCollection());

        boolean reset = adapter.reset();

        assertThat(reset).isFalse();
        assertThat(adapter.getCount()).isZero();
    }

    @Test
    public void reset_returnsFalse_whenNoInteractionsWithAdapter() {
        boolean reset = adapter.reset();

        assertThat(reset).isFalse();
    }

    @Test
    public void update_returnsFalse_whenAdapterContainsSameItems_inSameOrder() {
        Collection<String> stringCollectionA = rangeStringCollection(0, 5);
        Collection<String> stringCollectionB = rangeStringCollection(0, 5);
        adapter.append(stringCollectionA);

        boolean changed = adapter.update(stringCollectionB);

        assertThat(changed).isFalse();
    }

    @Test
    public void update_replacesItems_whenAdapterContainsSameItems_inDifferentOrder() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(5, 0);
        adapter.append(stringCollectionA);

        boolean changed = adapter.update(stringCollectionB);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollectionB, adapter);
    }

    @Test
    public void update_replacesItems_whenAdapterContainsDifferentItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(6, 10);
        adapter.append(stringCollectionA);

        boolean changed = adapter.update(stringCollectionB);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollectionB, adapter);
    }

    @Test
    public void update_emptiesAdapter_whenAdapterHasItems_andUpdatedWithEmptyList() {
        adapter.append(rangeStringCollection(0, 5));

        boolean changed = adapter.update(emptyStringCollection());

        assertThat(changed).isTrue();
        assertThat(adapter.getCount()).isZero();
    }

    @Test
    public void update_putsItemsInAdapter_whenAdapterIsEmpty() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        boolean changed = adapter.update(stringCollection);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollection, adapter);
    }

    @Test
    public void append_appendsItemsInAdapter_whenAdapterIsEmpty() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        boolean appended = adapter.append(stringCollection);

        assertThat(appended).isTrue();
        assertAdapterItems(stringCollection, adapter);
    }

    @Test
    public void append_appendsItems_whenAdapterHasDifferentItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(6, 10);
        adapter.append(stringCollectionA);

        boolean appended = adapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, adapter);
    }

    @Test
    public void append_appendsItems_whenAdapterHasSameItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(0, 5);
        adapter.append(stringCollectionA);

        boolean appended = adapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, adapter);
    }

    @Test
    public void append_appendsItems_whenAdapterContainsItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 10);
        List<String> stringCollectionB = rangeStringCollection(0, 5);
        adapter.append(stringCollectionA);

        boolean appended = adapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, adapter);
    }

    @Test
    public void append_doesNotChangeAdapterItems_whenEmptyCollectionIsAppended() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        adapter.append(stringCollection);

        boolean appended = adapter.append(emptyStringCollection());

        assertThat(appended).isFalse();
        assertAdapterItems(stringCollection, adapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_throws_whenPositionIsTooBig() {
        adapter.insert("", adapter.getCount());
    }

    @Test
    public void insert_insertsItemInPosition() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        String item = "10";
        adapter.append(stringCollection);

        boolean inserted = adapter.insert(item, 2);

        assertThat(inserted).isTrue();
        assertThat(adapter.getCount()).isEqualTo(stringCollection.size() + 1);
        assertThat(adapter.getItem(2)).isEqualTo(ofObj(item));
    }

    @Test(expected = IllegalArgumentException.class)
    public void remove_throws_whenPositionIsTooBig() {
        adapter.remove(adapter.getCount());
    }

    @Test
    public void remove_removesItemInPosition() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        adapter.append(stringCollection);

        boolean removed = adapter.remove(2);

        assertThat(removed).isTrue();
        assertThat(adapter.getCount()).isEqualTo(stringCollection.size() - 1);
    }

    @Test
    public void remove_removesItem_whenExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        adapter.append(stringCollection);

        boolean removed = adapter.remove("2");

        assertThat(removed).isTrue();
        assertThat(adapter.getCount()).isEqualTo(stringCollection.size() - 1);
    }

    @Test
    public void remove_doesNotRemove_whenItemDoesNotExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        adapter.append(stringCollection);

        boolean removed = adapter.remove("10");

        assertThat(removed).isFalse();
        assertThat(adapter.getCount()).isEqualTo(stringCollection.size());
    }

    @Test
    public void removeAll_removesItems_whenTheyExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(0, 2);
        adapter.append(stringCollection);

        boolean removed = adapter.removeAll(removeCollection);

        assertThat(removed).isTrue();
        assertThat(adapter.getCount())
                .isEqualTo(stringCollection.size() - removeCollection.size());
    }

    @Test
    public void removeAll_removesItems_whenOnlySomeOfThemExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(3, 8);
        adapter.append(stringCollection);

        boolean removed = adapter.removeAll(removeCollection);

        assertThat(removed).isTrue();
        assertThat(adapter.getCount()).isEqualTo(3);
    }

    @Test
    public void removeAll_doesNotRemove_whenItemsDoNotExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(6, 10);
        adapter.append(stringCollection);

        boolean removed = adapter.removeAll(removeCollection);

        assertThat(removed).isFalse();
        assertThat(adapter.getCount()).isEqualTo(stringCollection.size());
    }

    @Test
    public void getCount_returnsZero_whenNoInteractionsWithAdapter() {
        assertThat(adapter.getCount()).isZero();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getItem_throws_whenPositionIsTooBig() {
        adapter.getItem(adapter.getCount());
    }

    @Test
    public void getItem_returnsNone_whenItemAtPositionIsNull() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        stringCollection.set(0, null);

        adapter.append(stringCollection);

        assertThat(adapter.getItem(0)).isEqualTo(NONE);
    }

    @Test
    public void getItem_returnsOptionOfItem_whenThePositionIsValid() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        adapter.append(stringCollection);

        assertThat(adapter.getItem(2)).isEqualTo(ofObj(stringCollection.get(2)));
    }

    @Test
    public void getItemPosition_returnsOptionOfPosition_whenItemExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        adapter.append(stringCollection);

        assertThat(adapter.getItemPosition("2")).isEqualTo(ofObj(2));
    }

    @Test
    public void getItemPosition_returnsNone_whenItemDoesNotExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        adapter.append(stringCollection);

        assertThat(adapter.getItemPosition("10")).isEqualTo(NONE);
    }

    private static List<String> rangeStringCollection(final int from, final int to) {
        return Observable.range(from, to)
                         .map(Object::toString)
                         .toList()
                         .blockingGet();
    }

    private static Collection<String> emptyStringCollection() {
        return Collections.emptyList();
    }

    private static <T> void assertAdapterItems(@NonNull final List<T> items,
                                               @NonNull final AdapterInteractor<T> adapter) {
        assertThat(adapter.getCount()).isEqualTo(items.size());

        for (int i = 0; i < items.size(); i++) {
            assertThat(adapter.getItem(i)).isEqualTo(ofObj(items.get(i)));
        }
    }
}
