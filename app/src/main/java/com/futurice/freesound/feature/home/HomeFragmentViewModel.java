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

package com.futurice.freesound.feature.home;

import com.futurice.freesound.network.api.model.User;
import com.futurice.freesound.viewmodel.SimpleViewModel;

import android.support.annotation.NonNull;

import io.reactivex.Single;

import static com.futurice.freesound.common.utils.Preconditions.get;

final class HomeFragmentViewModel extends SimpleViewModel {

    @NonNull
    private final Single<User> homeUser;

    HomeFragmentViewModel(@NonNull final UserDataModel userDataModel) {
        this.homeUser = get(userDataModel).getHomeUser()
                                          .cache();
    }

    @NonNull
    Single<String> getImage() {
        return homeUser.map(it -> it.avatar().large());
    }

    @NonNull
    Single<String> getUserName() {
        return homeUser.map(User::username);
    }

    @NonNull
    Single<String> getAbout() {
        return homeUser.map(User::about);
    }
}
