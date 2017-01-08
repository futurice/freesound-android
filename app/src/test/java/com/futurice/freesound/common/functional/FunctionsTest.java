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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class FunctionsTest {

    @Test
    public void nothing0_hasSingleInstance() {
        assertThat(Functions.nothing0()).isSameAs(Functions.nothing0());
    }

    @Test
    public void nothing1_doesNotInteractWithParameter() throws Exception {
        Object value = mock(Object.class);

        Functions.nothing1().accept(value);

        verifyZeroInteractions(value);
    }

    @Test
    public void nothing1_hasSingleInstance() throws Exception {
        assertThat(Functions.nothing1()).isSameAs(Functions.nothing1());
    }

    @Test
    public void alwaysTrue_hasSingleInstance() {
        assertThat(Functions.alwaysTrue()).isSameAs(Functions.alwaysTrue());
    }

    @Test
    public void alwaysTrue_returnsTrue_whenParameterTrue() throws Exception {
        assertThat(Functions.alwaysTrue().test(true)).isTrue();
    }

    @Test
    public void alwaysTrue_returnsTrue_whenParameterIsFalse() throws Exception {
        assertThat(Functions.alwaysTrue().test(false)).isTrue();
    }

    @Test
    public void alwaysFalse_hasSingleInstance() {
        assertThat(Functions.alwaysFalse()).isSameAs(Functions.alwaysFalse());
    }

    @Test
    public void alwaysFalse_returnsFalse_whenParameterTrue() throws Exception {
        assertThat(Functions.alwaysFalse().test(true)).isFalse();
    }

    @Test
    public void alwaysFalse_returnsFalse_whenParameterIsFalse() throws Exception {
        assertThat(Functions.alwaysFalse().test(false)).isFalse();
    }

    @Test
    public void identity_hasSingleInstance() {
        assertThat(Functions.identity()).isSameAs(Functions.identity());
    }

    @Test
    public void identity_doesNotInteractWithParameter() throws Exception {
        Object value = mock(Object.class);

        Functions.identity().apply(value);

        verifyZeroInteractions(value);
    }

    @Test
    public void identity_returnWithParameterValue() throws Exception {
        Object value = mock(Object.class);

        assertThat(Functions.identity().apply(value)).isSameAs(value);
    }

    @Test
    public void invert_hasSingleInstance() {
        assertThat(Functions.invert()).isSameAs(Functions.invert());
    }

    @Test
    public void invert_returnsFalse_whenParameterIsTrue() throws Exception {
        assertThat(Functions.invert().apply(true)).isFalse();
    }

    @Test
    public void invert_returnsTrue_whenParameterIsFalse() throws Exception {
        assertThat(Functions.invert().apply(false)).isTrue();
    }

    @Test
    public void ifTrue_hasSingleInstance() {
        assertThat(Functions.ifTrue()).isSameAs(Functions.ifTrue());
    }

    @Test
    public void ifTrue_returnsFalse_whenParameterIsFalse() throws Exception {
        assertThat(Functions.ifTrue().apply(false)).isFalse();
    }

    @Test
    public void ifTrue_returnsTrue_whenParameterIsTrue() throws Exception {
        assertThat(Functions.ifTrue().apply(true)).isTrue();
    }

    @Test
    public void ifFalse_hasSingleInstance() {
        assertThat(Functions.ifFalse()).isSameAs(Functions.ifFalse());
    }

    @Test
    public void ifFalse_returnsFalse_whenParameterIsTrue() throws Exception {
        assertThat(Functions.ifFalse().apply(true)).isFalse();
    }

    @Test
    public void ifFalse_returnsTrue_whenParameterIsFalse() throws Exception {
        assertThat(Functions.ifFalse().apply(false)).isTrue();
    }

}
