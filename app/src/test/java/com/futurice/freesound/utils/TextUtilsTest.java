package com.futurice.freesound.utils;

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

}
