package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;

import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

final class SoundItemViewModel_Factory {

    private final Navigator navigator;

    SoundItemViewModel_Factory(@NonNull final Navigator navigator) {
        this.navigator = get(navigator);
    }

    @NonNull
    SoundItemViewModel create(@NonNull final Sound sound) {
        return new SoundItemViewModel(sound, navigator);
    }
}
