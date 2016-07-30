package com.futurice.freesound.feature.common;

import android.support.annotation.NonNull;

/**
 * Aggregates and sends events to analytics service.
 */
public interface Analytics {

    /**
     * Sends event to analytics service.
     *
     * @param event Event name
     */
    void log(@NonNull String event);
}
