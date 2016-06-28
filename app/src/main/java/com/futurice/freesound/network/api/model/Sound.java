package com.futurice.freesound.network.api.model;

import com.google.auto.value.AutoValue;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class Sound implements Parcelable {

    // The sound’s unique identifier.
    @Nullable
    public abstract Long id();

    // The URI for this sound on the Freesound website.
    @Nullable
    public abstract String url();

    // The name user gave to the sound.
    @Nullable
    public abstract String name();

    // An array of tags the user gave to the sound.
    @Nullable
    public abstract List<String> tags();

    // The description the user gave to the sound.
    @Nullable
    public abstract String description();

    // Latitude and longitude of the geotag separated by spaces
    // (e.g. “41.0082325664 28.9731252193”, only for sounds that have been geotagged).
    @Nullable
    public abstract GeoLocation geotag();

    // The username of the uploader of the sound.
    @Nullable
    public abstract String username();

    // Dictionary including the URIs for spectrogram and waveform visualizations of the sound.
    // The dictionary includes the fields waveform_l and waveform_m (for large and medium waveform
    // images respectively), and spectral_l and spectral_m (for large and medium spectrogram images
    // respectively).
    @Nullable
    public abstract Map<SoundImageFormat, String> images();

}
