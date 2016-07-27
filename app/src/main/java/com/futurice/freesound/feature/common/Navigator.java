package com.futurice.freesound.feature.common;

import com.futurice.freesound.network.api.model.Sound;

import android.support.annotation.NonNull;

public interface Navigator {

    void openSearch();

    void openSoundDetails(@NonNull Sound sound);

}
