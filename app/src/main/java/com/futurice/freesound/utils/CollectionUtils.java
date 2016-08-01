package com.futurice.freesound.utils;

import android.support.annotation.NonNull;

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
    public static <T> boolean areEqual(@NonNull final Collection<T> first,
                                       @NonNull final Collection<T> second) {
        if (first.size() != second.size()) {
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
