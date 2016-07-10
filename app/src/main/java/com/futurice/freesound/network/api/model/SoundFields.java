package com.futurice.freesound.network.api.model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.futurice.freesound.utils.Preconditions.get;

/**
 * TODO This could be auto-generated.
 */
public final class SoundFields {

    public static final SoundFields BASE = new SoundFields.Builder()
            .id()
            .url()
            .name()
            .tags()
            .description()
            .geotag()
            .username()
            .images()
            .build();

    private static final String ID = "id";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String TAGS = "tags";
    private static final String DESCRIPTION = "description";
    private static final String GEOTAG = "geotag";
    private static final String USERNAME = "username";
    private static final String IMAGES = "images";

    @NonNull
    private final Set<String> fields;

    private SoundFields(@NonNull Set<String> fields) {
        this.fields = get(fields);
    }

    @Override
    public String toString() {
        return RequestHelper.asCommaSeparated(fields);
    }

    private static class Builder {

        @NonNull
        private final Set<String> fields = new HashSet<>();

        Builder id() {
            add(ID);
            return this;
        }

        Builder url() {
            add(URL);
            return this;
        }

        Builder name() {
            add(NAME);
            return this;
        }

        Builder tags() {
            add(TAGS);
            return this;
        }

        Builder description() {
            add(DESCRIPTION);
            return this;
        }

        Builder geotag() {
            add(GEOTAG);
            return this;
        }

        Builder username() {
            add(USERNAME);
            return this;
        }

        Builder images() {
            add(IMAGES);
            return this;
        }

        private void add(String field) {
            fields.add(field);
        }

        SoundFields build() {
            return new SoundFields(Collections.unmodifiableSet(fields));
        }

    }

}
