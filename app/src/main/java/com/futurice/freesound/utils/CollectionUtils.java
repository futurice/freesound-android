package com.futurice.freesound.utils;

import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;

public final class CollectionUtils {

    /**
     * Verifies if the content of the collections is the same.
     *
     * @param first  Collection
     * @param second Collection
     * @return True if the content and the order of the collection are equal, otherwise false
     */
    public static <T> boolean areEqual(@Nullable final Collection<T> first,
                                       @Nullable final Collection<T> second) {
        if (first == null || second == null || first.size() != second.size()) {
            return false;
        }

        Iterator<T> firstIterator = first.iterator();
        Iterator<T> secondIterator = second.iterator();
        while (firstIterator.hasNext()) {
            if (!firstIterator.next().equals(secondIterator.next())) {
                return false;
            }
        }
        return true;
    }
}
