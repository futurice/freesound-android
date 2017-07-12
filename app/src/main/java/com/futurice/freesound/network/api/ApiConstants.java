/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.futurice.freesound.network.api;

import com.futurice.freesound.common.InstantiationForbiddenError;

/**
 * Constants used in the Freesound API.
 */
public final class ApiConstants {

    // Authentication
    static final String TOKEN_QUERY_PARAM = "token";
    static final String AUTHORIZATION_CODE_GRANT_TYPE_VALUE = "authorization_code";

    // The pattern used is a compromise because the Freesound API data pattern inconsistent.
    // Some Sound objects have up to 6 decimal places for the seconds value, whereas others
    // don't have any.
    //
    // This pattern might not be sufficient for fields that require high accuracy because it
    // truncates sub-second values.
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private ApiConstants() {
        throw new InstantiationForbiddenError();
    }
}
