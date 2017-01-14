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

package com.futurice.freesound.common.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextUtilsTest {

    @Test
    public void isNotNullOrEmpty_returnFalse_whenEmptyString() {
        assertThat(TextUtils.isNotNullOrEmpty("")).isFalse();
    }

    @Test
    public void isNotNullOrEmpty_returnFalse_whenNull() {
        assertThat(TextUtils.isNotNullOrEmpty(null)).isFalse();
    }

    @Test
    public void isNotNullOrEmpty_returnTrue_whenString() {
        assertThat(TextUtils.isNotNullOrEmpty("dummy")).isTrue();
    }

    @Test
    public void isNullOrEmpty_returnTrue_whenEmptyString() {
        assertThat(TextUtils.isNullOrEmpty("")).isTrue();
    }

    @Test
    public void isNullOrEmpty_returnTrue_whenNull() {
        assertThat(TextUtils.isNullOrEmpty(null)).isTrue();
    }

    @Test
    public void isNullOrEmpty_returnFalse_whenString() {
        assertThat(TextUtils.isNullOrEmpty("dummy")).isFalse();
    }

    @Test
    public void isNotEmpty_returnFalse_whenNull() {
        assertThat(TextUtils.isNotEmpty(null)).isFalse();
    }

    @Test
    public void isNotEmpty_returnTrue_whenNonEmptyString() {
        assertThat(TextUtils.isNotEmpty("dummy")).isTrue();
    }

    @Test
    public void isNotEmpty_returnFalse_whenEmptyString() {
        assertThat(TextUtils.isNotEmpty("")).isFalse();
    }

}
