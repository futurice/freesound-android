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

package com.futurice.freesound.common.functional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringFunctionsTest {

    @Test
    public void isEmpty_returnsTrue_whenCalled_withEmptyString() throws Exception {
        assertThat(StringFunctions.isEmpty().test("")).isTrue();
    }

    @Test
    public void isEmpty_returnsTrue_whenCalled_withNull() throws Exception {
        assertThat(StringFunctions.isEmpty().test(null)).isTrue();
    }

    @Test
    public void isEmpty_returnsFalse_whenCalled_withString() throws Exception {
        assertThat(StringFunctions.isEmpty().test("dummy")).isFalse();
    }

    @Test
    public void isNotEmpty_returnsFalse_whenCalled_withEmptyString() throws Exception {
        assertThat(StringFunctions.isNotEmpty().test("")).isFalse();
    }

    @Test
    public void isNotEmpty_returnsFalse_whenCalled_withNull() throws Exception {
        assertThat(StringFunctions.isNotEmpty().test(null)).isFalse();
    }

    @Test
    public void isNotEmpty_returnsTrue_whenCalled_withString() throws Exception {
        assertThat(StringFunctions.isNotEmpty().test("dummy")).isTrue();
    }

}
