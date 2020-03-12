package com.futurice.freesound.feature.search;

import androidx.annotation.NonNull;

import com.futurice.freesound.feature.audio.AudioPlayer;
import com.futurice.freesound.feature.common.Navigator;
import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.Sound;

final class SoundItemViewModelFactory {
    @NonNull
    private final Navigator navigator;
    @NonNull
    private final AudioPlayer audioPlayer;
    @NonNull
    private final FreeSoundApiService freeSoundApiService;

    SoundItemViewModelFactory(@NonNull Navigator navigator,
                              @NonNull AudioPlayer audioPlayer,
                              @NonNull FreeSoundApiService freeSoundApiService) {
        this.navigator = navigator;
        this.audioPlayer = audioPlayer;
        this.freeSoundApiService = freeSoundApiService;
    }

    public SoundItemViewModel create(Sound sound) {
        return new SoundItemViewModel(sound, navigator, audioPlayer, freeSoundApiService);
    }
}
