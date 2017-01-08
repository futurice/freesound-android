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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class PreconditionsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void checkNotNull_doesNotThrowException_whenNonNull() {

        Preconditions.checkNotNull(new Object());
    }

    @Test
    public void checkNotNull_throwsNullPointerException_whenNull() {
        thrown.expect(NullPointerException.class);

        Preconditions.checkNotNull(null);
    }

    @Test
    public void checkNotNullWithMessage_doesNotThrowException_whenNonNull() {
        Preconditions.checkNotNull(new Object(), "Unused message");
    }

    @Test
    public void checkNotNullWithMessage_throwsNullPointerExceptionWithMessage_whenNull() {
        final String message = "message";
        thrown.expect(NullPointerException.class);
        thrown.expectMessage(message);

        Preconditions.checkNotNull(null, message);
    }

    @Test
    public void get_returnsParameter_whenNonNull() {
        Object obj = new Object();

        assertThat(Preconditions.get(obj)).isEqualTo(obj);
    }

    @Test
    public void get_throwsNullPointerException_whenNull() {
        thrown.expect(NullPointerException.class);

        Preconditions.get(null);
    }

}
