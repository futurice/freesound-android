package com.futurice.freesound.feature.common;

import com.futurice.freesound.feature.search.SearchActivity;
import com.futurice.freesound.inject.activity.Activity;
import com.futurice.freesound.network.api.model.Sound;

import android.content.Context;
import android.support.annotation.NonNull;

import static com.futurice.freesound.utils.Preconditions.get;

public final class DefaultNavigator implements Navigator {

    @NonNull
    private final Context context;

    public DefaultNavigator(@Activity @NonNull final Context context) {
        this.context = get(context);
    }

    @Override
    public void openSearch() {
        SearchActivity.open(context);
    }

    @Override
    public void openSoundDetails(@NonNull final Sound sound) {
        // TODO Via the DetailsActivity.open
    }
}
