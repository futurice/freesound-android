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

package com.futurice.freesound.common.functional;

import org.junit.Test;

import polanski.option.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class OptionFunctionsTest {

    @Test
    public void nothing0_hasSingleInstance() {
        assertThat(OptionFunctions.nothing0()).isSameAs(OptionFunctions.nothing0());
    }

    @Test
    public void nothing1_doesNotInteractWithParameter() {
        Object value = mock(Object.class);

        OptionFunctions.nothing1().call(value);

        verifyZeroInteractions(value);
    }

    @Test
    public void nothing1_hasSingleInstance() {
        assertThat(OptionFunctions.nothing1()).isSameAs(OptionFunctions.nothing1());
    }

    @Test
    public void alwaysTrue_hasSingleInstance() {
        assertThat(OptionFunctions.alwaysTrue()).isSameAs(OptionFunctions.alwaysTrue());
    }

    @Test
    public void alwaysTrue_returnsTrue_whenParameterTrue() {
        assertThat(OptionFunctions.alwaysTrue().call(true)).isTrue();
    }

    @Test
    public void alwaysTrue_returnsTrue_whenParameterIsFalse() {
        assertThat(OptionFunctions.alwaysTrue().call(false)).isTrue();
    }

    @Test
    public void alwaysFalse_hasSingleInstance() {
        assertThat(OptionFunctions.alwaysFalse()).isSameAs(OptionFunctions.alwaysFalse());
    }

    @Test
    public void alwaysFalse_returnsFalse_whenParameterTrue() {
        assertThat(OptionFunctions.alwaysFalse().call(true)).isFalse();
    }

    @Test
    public void alwaysFalse_returnsFalse_whenParameterIsFalse() {
        assertThat(OptionFunctions.alwaysFalse().call(false)).isFalse();
    }

    @Test
    public void identity_hasSingleInstance() {
        assertThat(OptionFunctions.identity()).isSameAs(OptionFunctions.identity());
    }

    @Test
    public void identity_doesNotInteractWithParameter() {
        Object value = mock(Object.class);

        OptionFunctions.identity().call(value);

        verifyZeroInteractions(value);
    }

    @Test
    public void identity_returnWithParameterValue() {
        Object value = mock(Object.class);

        assertThat(OptionFunctions.identity().call(value)).isSameAs(value);
    }

    @Test
    public void invert_hasSingleInstance() {
        assertThat(OptionFunctions.invert()).isSameAs(OptionFunctions.invert());
    }

    @Test
    public void invert_returnsFalse_whenParameterIsTrue() {
        assertThat(OptionFunctions.invert().call(true)).isFalse();
    }

    @Test
    public void invert_returnsTrue_whenParameterIsFalse() {
        assertThat(OptionFunctions.invert().call(false)).isTrue();
    }

    @Test
    public void ifTrue_hasSingleInstance() {
        assertThat(OptionFunctions.ifTrue()).isSameAs(OptionFunctions.ifTrue());
    }

    @Test
    public void ifTrue_returnsFalse_whenParameterIsFalse() {
        assertThat(OptionFunctions.ifTrue().call(false)).isFalse();
    }

    @Test
    public void ifTrue_returnsTrue_whenParameterIsTrue() {
        assertThat(OptionFunctions.ifTrue().call(true)).isTrue();
    }

    @Test
    public void ifFalse_hasSingleInstance() {
        assertThat(OptionFunctions.ifFalse()).isSameAs(OptionFunctions.ifFalse());
    }

    @Test
    public void ifFalse_returnsFalse_whenParameterIsTrue() {
        assertThat(OptionFunctions.ifFalse().call(true)).isFalse();
    }

    @Test
    public void ifFalse_returnsTrue_whenParameterIsFalse() {
        assertThat(OptionFunctions.ifFalse().call(false)).isTrue();
    }

    @Test
    public void toNone_hasSingleInstance() {
        assertThat(OptionFunctions.toNone()).isSameAs(OptionFunctions.toNone());
    }

    @Test
    public void toNone_returnsNone_whenParameterIsNonNull() {
        assertThat(OptionFunctions.toNone().call(mock(Object.class))).isEqualTo(Option.NONE);
    }

    @Test
    public void ifFalse_returnsTrue_whenParameterIsNull() {
        assertThat(OptionFunctions.toNone().call(null)).isEqualTo(Option.NONE);
    }

}
