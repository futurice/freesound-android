package com.futurice.freesound.network.api.model;

import android.support.annotation.NonNull;

import java.util.Iterator;

import static com.futurice.freesound.utils.Preconditions.checkNotNull;

final class RequestHelper {

    @NonNull
    static <T> String asCommaSeparated(@NonNull final Iterable<T> iterable) {
        checkNotNull(iterable);

        StringBuilder sb = new StringBuilder();
        Iterator<T> iter = iterable.iterator();
        while (iter.hasNext()) {
            String value = iter.next().toString();
            sb.append(value);
            if (iter.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private RequestHelper() {
        throw new AssertionError("No instances allowed");
    }
}
