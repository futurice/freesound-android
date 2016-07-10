package com.futurice.freesound.feature.common;

import com.futurice.freesound.inject.activity.Activity;
import com.futurice.freesound.network.api.model.Sound;

import android.content.Context;
import android.support.annotation.NonNull;

public final class DefaultNavigator implements Navigator {

    private final Context context;

    public DefaultNavigator(@Activity @NonNull final Context context) {
        this.context = context;
    }

    @Override
    public void openSoundDetails(@NonNull final Sound sound) {
        // TODO Via the DetailsActivity.open
    }
}
