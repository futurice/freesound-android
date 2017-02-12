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

import com.futurice.freesound.network.api.FreeSoundApiService;
import com.futurice.freesound.network.api.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultUserDataModelTest {

    @Mock
    private FreeSoundApiService freeSoundApiService;

    private DefaultUserDataModel dataModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        dataModel = new DefaultUserDataModel(freeSoundApiService);
    }

    @Test
    public void getHomeUser_looksForSpiceProgram() {
        dataModel.getHomeUser();

        verify(freeSoundApiService).getUser(DefaultUserDataModel.USER_NAME);
    }

    @Test
    public void getHomeUser_returnsResultOfSearch() {
        User result = mock(User.class);
        new ArrangeBuilder()
                .withUser(result);

        dataModel.getHomeUser()
                 .test()
                 .assertValue(result);
    }

    private class ArrangeBuilder {

        ArrangeBuilder withUser(User user) {
            when(freeSoundApiService.getUser(anyString())).thenReturn(Single.just(user));
            return this;
        }
    }

}
