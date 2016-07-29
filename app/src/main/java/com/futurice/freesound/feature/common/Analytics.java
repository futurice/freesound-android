package com.futurice.freesound.feature.common;

import android.support.annotation.NonNull;

public interface Analytics {

    void log(@NonNull String event);
}
