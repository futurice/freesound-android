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

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

import io.reactivex.Completable;
import io.reactivex.Observable;
import polanski.option.Option;

final class OAuthLoginProvider {

    @NonNull
    Completable attemptAuthorize(Activity fromActivity) {
        return Completable.fromAction(() -> openAuthorizationUrl(fromActivity);
    }

    @NonNull
    Completable handleAuthorizationResponse(@NonNull final Option<String> code,
                                            @NonNull final Option<String> error) {

        if (code.isSome()) {

        } else if (error.isSome()) {

        } else {
            cancel();
        }
        // Either allow or denied.
        // If allow then make request to API to get token.
        // If deny show toast saying Login canceled?
        // Need to show progress for login from the beginning (attemptAuthorize) so that it is smooth and continuous
        //

    }

    Observable<Fetch<>>

    void cancel() {

    }

    private void openAuthorizationUrl(Activity activity) {
        Uri appAuthUrl = Uri.parse("https://www.freesound.org/apiv2/oauth2/authorize/")
                            .buildUpon()
                            .appendQueryParameter("client_id", "actualClientId")
                            .appendQueryParameter("response_type", "code")
                            .build();
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .build();
        customTabsIntent.launchUrl(activity, appAuthUrl);
    }

    // Ideally expose request status as

    // Data
    // In Progress
    // Failed

    // Have "SDK" that takes Activity as parameters
    // Gives callbacks which are actually redirected launched Activity
    // Make these callbacks actually reactive.
}
