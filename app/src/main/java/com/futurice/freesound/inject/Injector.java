package com.futurice.freesound.inject;

import android.support.annotation.NonNull;

public interface Injector<T> {

    @NonNull
    T component();

    void inject();
}
