package com.futurice.freesound.ui.adapter;

import com.futurice.freesound.ui.adapter.base.DefaultAdapterInteractor;

import org.junit.Before;
import org.junit.Test;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static polanski.option.Option.NONE;
import static polanski.option.Option.ofObj;

public class AdapterInteractorTest {

    private AdapterInteractor<String> mAdapter;

    @Before
    public void setUp() {
        mAdapter = new AdapterInteractor<>();
    }

    @Test
    public void reset_emptiesTheAdapter_whenAdapterIsNotEmpty() {
        mAdapter.append(rangeStringCollection(0, 5));

        boolean reset = mAdapter.reset();

        assertThat(reset).isTrue();
        assertThat(mAdapter.getCount()).isZero();
    }

    @Test
    public void reset_returnsFalse_whenAdapterIsEmpty() {
        mAdapter.append(emptyStringCollection());

        boolean reset = mAdapter.reset();

        assertThat(reset).isFalse();
        assertThat(mAdapter.getCount()).isZero();
    }

    @Test
    public void reset_returnsFalse_whenNoInteractionsWithAdapter() {
        boolean reset = mAdapter.reset();

        assertThat(reset).isFalse();
    }

    @Test
    public void update_returnsFalse_whenAdapterContainsSameItems_inSameOrder() {
        Collection<String> stringCollectionA = rangeStringCollection(0, 5);
        Collection<String> stringCollectionB = rangeStringCollection(0, 5);
        mAdapter.append(stringCollectionA);

        boolean changed = mAdapter.update(stringCollectionB);

        assertThat(changed).isFalse();
    }

    @Test
    public void update_replacesItems_whenAdapterContainsSameItems_inDifferentOrder() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(5, 0);
        mAdapter.append(stringCollectionA);

        boolean changed = mAdapter.update(stringCollectionB);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollectionB, mAdapter);
    }

    @Test
    public void update_replacesItems_whenAdapterContainsDifferentItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(6, 10);
        mAdapter.append(stringCollectionA);

        boolean changed = mAdapter.update(stringCollectionB);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollectionB, mAdapter);
    }

    @Test
    public void update_emptiesAdapter_whenAdapterHasItems_andUpdatedWithEmptyList() {
        mAdapter.append(rangeStringCollection(0, 5));

        boolean changed = mAdapter.update(emptyStringCollection());

        assertThat(changed).isTrue();
        assertThat(mAdapter.getCount()).isZero();
    }

    @Test
    public void update_putsItemsInAdapter_whenAdapterIsEmpty() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        boolean changed = mAdapter.update(stringCollection);

        assertThat(changed).isTrue();
        assertAdapterItems(stringCollection, mAdapter);
    }

    @Test
    public void append_appendsItemsInAdapter_whenAdapterIsEmpty() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        boolean appended = mAdapter.append(stringCollection);

        assertThat(appended).isTrue();
        assertAdapterItems(stringCollection, mAdapter);
    }

    @Test
    public void append_appendsItems_whenAdapterHasDifferentItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(6, 10);
        mAdapter.append(stringCollectionA);

        boolean appended = mAdapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, mAdapter);
    }

    @Test
    public void append_appendsItems_whenAdapterHasSameItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 5);
        List<String> stringCollectionB = rangeStringCollection(0, 5);
        mAdapter.append(stringCollectionA);

        boolean appended = mAdapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, mAdapter);
    }

    @Test
    public void append_appendsItems_whenAdapterContainsItems() {
        List<String> stringCollectionA = rangeStringCollection(0, 10);
        List<String> stringCollectionB = rangeStringCollection(0, 5);
        mAdapter.append(stringCollectionA);

        boolean appended = mAdapter.append(stringCollectionB);

        List<String> expectedItems = new ArrayList<String>() {{
            addAll(stringCollectionA);
            addAll(stringCollectionB);
        }};
        assertThat(appended).isTrue();
        assertAdapterItems(expectedItems, mAdapter);
    }

    @Test
    public void append_doesNotChangeAdapterItems_whenEmptyCollectionIsAppended() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        mAdapter.append(stringCollection);

        boolean appended = mAdapter.append(emptyStringCollection());

        assertThat(appended).isFalse();
        assertAdapterItems(stringCollection, mAdapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insert_throws_whenPositionIsTooBig() {
        mAdapter.insert("", mAdapter.getCount());
    }

    @Test
    public void insert_insertsItemInPosition() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        String item = "10";
        mAdapter.append(stringCollection);

        boolean inserted = mAdapter.insert(item, 2);

        assertThat(inserted).isTrue();
        assertThat(mAdapter.getCount()).isEqualTo(stringCollection.size() + 1);
        assertThat(mAdapter.getItem(2)).isEqualTo(ofObj(item));
    }

    @Test(expected = IllegalArgumentException.class)
    public void remove_throws_whenPositionIsTooBig() {
        mAdapter.remove(mAdapter.getCount());
    }

    @Test
    public void remove_removesItemInPosition() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.remove(2);

        assertThat(removed).isTrue();
        assertThat(mAdapter.getCount()).isEqualTo(stringCollection.size() - 1);
    }

    @Test
    public void remove_removesItem_whenExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.remove("2");

        assertThat(removed).isTrue();
        assertThat(mAdapter.getCount()).isEqualTo(stringCollection.size() - 1);
    }

    @Test
    public void remove_doesNotRemove_whenItemDoesNotExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.remove("10");

        assertThat(removed).isFalse();
        assertThat(mAdapter.getCount()).isEqualTo(stringCollection.size());
    }

    @Test
    public void removeAll_removesItems_whenTheyExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(0, 2);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.removeAll(removeCollection);

        assertThat(removed).isTrue();
        assertThat(mAdapter.getCount())
                .isEqualTo(stringCollection.size() - removeCollection.size());
    }

    @Test
    public void removeAll_removesItems_whenOnlySomeOfThemExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(3, 8);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.removeAll(removeCollection);

        assertThat(removed).isTrue();
        assertThat(mAdapter.getCount()).isEqualTo(3);
    }

    @Test
    public void removeAll_doesNotRemove_whenItemsDoNotExist() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        List<String> removeCollection = rangeStringCollection(6, 10);
        mAdapter.append(stringCollection);

        boolean removed = mAdapter.removeAll(removeCollection);

        assertThat(removed).isFalse();
        assertThat(mAdapter.getCount()).isEqualTo(stringCollection.size());
    }

    @Test
    public void getCount_returnsZero_whenNoInteractionsWithAdapter() {
        assertThat(mAdapter.getCount()).isZero();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getItem_throws_whenPositionIsTooBig() {
        mAdapter.getItem(mAdapter.getCount());
    }

    @Test
    public void getItem_returnsNone_whenItemAtPositionIsNull() {
        List<String> stringCollection = rangeStringCollection(0, 5);
        stringCollection.set(0, null);

        mAdapter.append(stringCollection);

        assertThat(mAdapter.getItem(0)).isEqualTo(NONE);
    }

    @Test
    public void getItem_returnsOptionOfItem_whenThePositionIsValid() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        mAdapter.append(stringCollection);

        assertThat(mAdapter.getItem(2)).isEqualTo(ofObj(stringCollection.get(2)));
    }

    @Test
    public void getItemPosition_returnsOptionOfPosition_whenItemExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        mAdapter.append(stringCollection);

        assertThat(mAdapter.getItemPosition("2")).isEqualTo(ofObj(2));
    }

    @Test
    public void getItemPosition_returnsNone_whenItemDoesNotExists() {
        List<String> stringCollection = rangeStringCollection(0, 5);

        mAdapter.append(stringCollection);

        assertThat(mAdapter.getItemPosition("10")).isEqualTo(NONE);
    }

    private static List<String> rangeStringCollection(final int from, final int to) {
        return Observable.range(from, to)
                         .map(Object::toString)
                         .toList()
                         .toBlocking()
                         .single();
    }

    private static Collection<String> emptyStringCollection() {
        return Collections.emptyList();
    }

    private static <T> void assertAdapterItems(@NonNull final List<T> items,
                                               @NonNull final DefaultAdapterInteractor<T> adapter) {
        assertThat(adapter.getCount()).isEqualTo(items.size());

        for (int i = 0; i < items.size(); i++) {
            assertThat(adapter.getItem(i)).isEqualTo(ofObj(items.get(i)));
        }
    }
}