package com.futurice.freesound.test.assertion;

import org.assertj.core.api.Condition;

import java.util.List;

public class IsEmptyListCondition<T> extends Condition<List<T>> {

    public static <T> IsEmptyListCondition<T> empty() {
        return new IsEmptyListCondition<>();
    }

    @Override
    public boolean matches(final List<T> value) {
        return value.isEmpty();
    }

}
