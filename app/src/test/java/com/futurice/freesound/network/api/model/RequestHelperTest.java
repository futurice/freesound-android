package com.futurice.freesound.network.api.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestHelperTest {

    @Test
    public void asCommaSeparated_isEmpty_whenEmptyIterable() {

        assertThat(RequestHelper.asCommaSeparated(Collections.emptyList())).isEmpty();
    }

    @Test
    public void asCommaSeparated_containsOnlyValue_whenSingleItem() {
        String value = "value";

        assertThat(RequestHelper.asCommaSeparated(Collections.singletonList(value)))
                .isEqualTo(value);
    }

    @Test
    public void asCommaSeparated_containCommasBetweenItems() {
        String value1 = "value1";
        String value2 = "value2";
        String[] values = {value1, value2};

        assertThat(RequestHelper.asCommaSeparated(Arrays.asList(values)))
                .isEqualTo(value1 + "," + value2);
    }

}
