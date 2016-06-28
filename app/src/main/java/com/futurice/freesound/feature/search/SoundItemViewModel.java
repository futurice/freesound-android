package com.futurice.freesound.feature.search;

import com.futurice.freesound.viewmodel.SimpleViewModel;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundImageFormat;

import android.support.annotation.NonNull;

import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

public class SoundItemViewModel extends SimpleViewModel {

    private final Sound sound;

    private final Navigator navigator;

    public SoundItemViewModel(@NonNull final Sound sound,
                              @NonNull final Navigator navigator) {
        this.sound = get(sound);
        this.navigator = get(navigator);
    }

    @NonNull
    public Observable<String> thumbnailImageUrl() {
        return Observable.just(sound.images().get(SoundImageFormat.waveform_m));
    }

    @NonNull
    public Observable<String> name() {
        return Observable.just(sound.name());
    }

    @NonNull
    public Observable<String> description() {
        return Observable.just(sound.description());
    }

    public void openDetails() {
        navigator.openSoundDetails(sound);
    }

}
