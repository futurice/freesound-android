/*
 * Copyright 2017 Futurice GmbH
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

package com.futurice.freesound.feature.login;

import com.futurice.freesound.common.InstantiationForbiddenError;

final class LoginConstants {

    static final String CLIENT_ID_AUTH_REQUEST_QUERY_PARAMETER = "client_id";
    static final String RESPONSE_TYPE_AUTH_REQUEST_QUERY_PARAMETER = "response_type";

    static final String CODE_AUTH_CALLBACK_QUERY_PARAMETER = "code";
    static final String ERROR_AUTH_CALLBACK_QUERY_PARAMETER = "error";

    private LoginConstants() {
        throw new InstantiationForbiddenError();
    }
}
