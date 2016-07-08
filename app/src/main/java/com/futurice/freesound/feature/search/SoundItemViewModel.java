package com.futurice.freesound.feature.search;

import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.model.Sound;
import com.futurice.freesound.network.api.model.SoundImageFormat;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import rx.Observable;

import static com.futurice.freesound.utils.Preconditions.get;

class SoundItemViewModel extends SimpleViewModel {

    @NonNull
    private final Sound sound;

    @NonNull
    private final Navigator navigator;

    SoundItemViewModel(@NonNull final Sound sound,
                       @NonNull final Navigator navigator) {
        this.sound = get(sound);
        this.navigator = get(navigator);
    }

    @NonNull
    Observable<String> thumbnailImageUrl() {
        return Observable.just(sound.images().get(SoundImageFormat.waveform_m));
    }

    @NonNull
    Observable<String> name() {
        return Observable.just(sound.name());
    }

    @NonNull
    Observable<String> description() {
        return Observable.just(sound.description());
    }

    void openDetails() {
        navigator.openSoundDetails(sound);
    }

}
