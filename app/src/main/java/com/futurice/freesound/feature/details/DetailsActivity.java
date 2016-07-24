package com.futurice.freesound.feature.details;

import com.futurice.freesound.inject.activity.ForActivity;
import com.futurice.freesound.network.api.model.Sound;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import static com.futurice.freesound.utils.Preconditions.get;

public class DetailsActivity extends AppCompatActivity {

    private static final String SOUND_PARAM = "sound";

    public static void open(@NonNull final @ForActivity Context context,
                            @NonNull final Sound sound) {
        Intent intent = new Intent(get(context), DetailsActivity.class);
        intent.putExtra(SOUND_PARAM, get(sound));
        context.startActivity(intent);
    }
}
