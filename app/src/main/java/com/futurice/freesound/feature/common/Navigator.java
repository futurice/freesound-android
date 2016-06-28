package com.futurice.freesound.feature.common;

import com.futurice.freesound.inject.activity.Activity;
import com.futurice.freesound.network.api.model.Sound;

import android.content.Context;
import android.support.annotation.NonNull;

public class Navigator {

    private final Context context;

    public Navigator(@Activity @NonNull final Context context) {
        this.context = context;
    }

    public void openSoundDetails(@NonNull final Sound sound) {
        // TODO Via the DetailsActivity.open
    }
}
