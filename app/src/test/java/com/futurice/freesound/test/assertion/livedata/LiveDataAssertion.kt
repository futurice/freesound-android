/*
 * Copyright 2018 Futurice GmbH
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

package com.futurice.freesound.test.assertion.livedata

import android.arch.lifecycle.Observer
import junit.framework.Assert.assertTrue
import junit.framework.TestCase.assertEquals

class TestObserver<T> : Observer<T>, LiveDataAssertion<T> {

    private val mutableValues: MutableList<T?>

    constructor() {
        mutableValues = ArrayList()
    }

    private constructor(values: MutableList<T?>) {
        TestObserver<T>()
        this.mutableValues = values
    }

    val values
        get() = mutableValues.toList()

    override fun onChanged(t: T?) {
        mutableValues.add(t)
    }

    fun getValue(): T? {
        return values.last()
    }

    fun skip(count: Int = 1): TestObserver<T> {
        if (count < 0) {
            throw IllegalArgumentException("Skip count parameter must be non-negative")
        }
        assertValueCountAtLeast("Cannot skip: $count value(s), when only: ${mutableValues.size} values", count + 1)

        return TestObserver(mutableValues.subList(count, mutableValues.lastIndex))
    }

    override fun assertValueCount(expectedCount: Int): LiveDataAssertion<T> {
        if (expectedCount < 0) {
            throw IllegalArgumentException("Expected count parameter must be non-negative")
        }

        return assertValueCount("Expected: $expectedCount values, but has: $1", expectedCount)
    }

    override fun assertOnlyValue(expected: T): LiveDataAssertion<T> {
        assertValueCount("Expected a single value, but has: $1", 1)

        val actual = values.last()
        assertEquals(expected, actual)
        return this
    }

    override fun assertValue(expected: T): LiveDataAssertion<T> {
        assertAtLeastSingleValueCount()

        val actual = values.last()
        assertEquals(expected, actual)
        return this
    }

    override fun assertValue(expectedPredicate: (T) -> Boolean): LiveDataAssertion<T> {
        assertAtLeastSingleValueCount()

        val actual = values.last()!!
        assertTrue(expectedPredicate(actual))
        return this
    }

    override fun assertValueAt(index: Int, expected: T): LiveDataAssertion<T> {
        val atLeastValueCount = index + 1
        assertValueCountAtLeast("Expected at least: $atLeastValueCount values, but has: $1", atLeastValueCount)

        val actual = values[index]
        assertEquals(expected, actual)
        return this
    }

    override fun assertNoValues(): LiveDataAssertion<T> {
        return assertValueCount("Expected no values, but has: $1", 0)
    }

    override fun assertValues(vararg expected: T): LiveDataAssertion<T> {
        assertEquals(expected, values)
        return this
    }

    override fun assertOnlyValues(vararg expected: T): LiveDataAssertion<T> {
        assertEquals(expected, values)
        return this
    }

    //
    // Internal Helpers
    //

    private fun assertValueCount(msg: String, expectedCount: Int): LiveDataAssertion<T> {
        val actualCount = values.size
        assertEquals(msg.format(actualCount), expectedCount, actualCount)
        return this
    }

    private fun assertValueCountAtLeast(msg: String, expectedCount: Int): LiveDataAssertion<T> {
        if (expectedCount < 0) {
            throw IllegalArgumentException("Expected count parameter must be non-negative")
        }

        val actualCount = values.size
        assertTrue(msg.format(actualCount), expectedCount <= actualCount)
        return this
    }

    private fun assertAtLeastSingleValueCount() {
        assertValueCountAtLeast("Expected at least one value.", 1)
    }

}

interface LiveDataAssertion<T> {

    fun assertNoValues(): LiveDataAssertion<T>
    fun assertValueCount(expectedCount: Int): LiveDataAssertion<T>
    fun assertValueAt(index: Int, expected: T): LiveDataAssertion<T>
    fun assertOnlyValue(expected: T): LiveDataAssertion<T>
    fun assertOnlyValues(vararg expected: T): LiveDataAssertion<T>
    fun assertValue(expected: T): LiveDataAssertion<T>
    fun assertValue(expectedPredicate: (T) -> Boolean): LiveDataAssertion<T>
    fun assertValues(vararg expected: T): LiveDataAssertion<T>
}

