package com.futurice.freesound.functional;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class FunctionsTest {

    @Test
    public void nothing1_doesNotInteractWithValue() {
        Object value = mock(Object.class);

        Functions.nothing1().call(value);

        verifyZeroInteractions(value);
    }

}
