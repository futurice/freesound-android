package com.futurice.freesound.functional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringFunctionsTest {

    @Test
    public void isEmpty_returnsTrue_whenCalled_withEmptyString() {
        assertThat(StringFunctions.isEmpty().call("")).isTrue();
    }

    @Test
    public void isEmpty_returnsTrue_whenCalled_withNull() {
        assertThat(StringFunctions.isEmpty().call(null)).isTrue();
    }

    @Test
    public void isEmpty_returnsFalse_whenCalled_withString() {
        assertThat(StringFunctions.isEmpty().call("dummy")).isFalse();
    }

    @Test
    public void isNotEmpty_returnsFalse_whenCalled_withEmptyString() {
        assertThat(StringFunctions.isNotEmpty().call("")).isFalse();
    }

    @Test
    public void isNotEmpty_returnsFalse_whenCalled_withNull() {
        assertThat(StringFunctions.isNotEmpty().call(null)).isFalse();
    }

    @Test
    public void isNotEmpty_returnsTrue_whenCalled_withString() {
        assertThat(StringFunctions.isNotEmpty().call("dummy")).isTrue();
    }

}
