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

package com.futurice.freesound.utils;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionUtilsTest {

    @Test
    public void areEqual_returnsTrue_whenTheCollectionsContainSameElementsInSameOrder() {
        Collection collection1 = createRangeCollection(0, 5);
        Collection collection2 = createRangeCollection(0, 5);

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isTrue();
    }

    @Test
    public void areEqual_returnsFalse_whenTheCollectionsHaveSameElementsInDifferentOrder() {
        Collection collection1 = createRangeCollection(0, 5);
        Collection collection2 = createRangeCollection(5, 0);

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isFalse();
    }

    @Test
    public void areEqual_returnsFalse_whenTheCollectionsContainDifferentElements() {
        Collection collection1 = createRangeCollection(0, 5);
        Collection collection2 = createRangeCollection(5, 10);

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isFalse();
    }

    @Test
    public void areEqual_returnsFalse_whenTheCollectionsHaveDifferentSizes() {
        Collection collection1 = createRangeCollection(0, 5);
        Collection collection2 = createRangeCollection(0, 4);

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isFalse();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void areEqual_returnsFalse_whenOneCollectionIsNull() {
        Collection collection1 = createRangeCollection(0, 5);
        Collection collection2 = null;

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isFalse();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void areEqual_returnsFalse_whenBothCollectionsAreNull() {
        Collection collection1 = null;
        Collection collection2 = null;

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isFalse();
    }

    @Test
    public void areEqual_returnsTrue_whenTheTwoCollectionsAreEmpty() {
        Collection collection1 = Collections.EMPTY_LIST;
        Collection collection2 = Collections.EMPTY_LIST;

        boolean areEqual = CollectionUtils.areEqual(collection1, collection2);

        assertThat(areEqual).isTrue();
    }

    private static Collection<Integer> createRangeCollection(final int start, final int end) {
        if (start <= end) {
            return Observable.range(start, end - start)
                             .toList()
                             .toBlocking()
                             .first();
        } else {
            return Observable.range(end, start - end)
                             .map(it -> (start + end) - it - 1)
                             .toList()
                             .toBlocking()
                             .first();
        }
    }
}
