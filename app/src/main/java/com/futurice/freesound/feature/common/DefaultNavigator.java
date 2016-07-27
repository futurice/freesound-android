package com.futurice.freesound.feature.common;

import com.futurice.freesound.feature.search.SearchActivity;
import com.futurice.freesound.network.api.model.Sound;

import android.app.Activity;
import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

public final class DefaultNavigator implements Navigator {

    @NonNull
    private final Activity activity;

    public DefaultNavigator(@NonNull final Activity activity) {
        this.activity = get(activity);
    }

    @Override
    public void openSearch() {
        SearchActivity.open(activity);
    }

    @Override
    public void openSoundDetails(@NonNull final Sound sound) {
        // TODO Via the DetailsActivity.open
    }

}
