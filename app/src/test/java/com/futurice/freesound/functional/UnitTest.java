package com.futurice.freesound.functional;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTest {

    @Test
    public void asUnit_returnsDefault_whenNonNull() {
        assertThat(Unit.asUnit(new Object())).isEqualTo(Unit.DEFAULT);
    }

    @Test(expected = NullPointerException.class)
    public void asUnit_throwsNpe_whenNull() {
        //noinspection ConstantConditions
        assertThat(Unit.asUnit(null));
    }

}
