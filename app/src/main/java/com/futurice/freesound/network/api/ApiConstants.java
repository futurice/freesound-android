package com.futurice.freesound.network.api;

final class ApiConstants {

    static final String TOKEN_QUERY_PARAM = "token";
    static final String IS_GEO_TAGGED_FILTER_QUERY_PARAM = "is_geotagged";

    private ApiConstants() {
        throw new AssertionError("No instances allowed");
    }
}
