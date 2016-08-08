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
