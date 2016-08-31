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

package com.futurice.freesound.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class FirebaseReleaseTreeTest {

    @Mock
    private FirebaseErrorReporter firebaseErrorReporter;

    private FirebaseReleaseTree firebaseReleaseTree;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.firebaseReleaseTree = new FirebaseReleaseTree(firebaseErrorReporter);
    }

    @Test
    public void log_doesNothing_whenThrowableIsNull() {
        firebaseReleaseTree.log(1, "dummyTag", "dummyMsg", null);

        verifyZeroInteractions(firebaseErrorReporter);
    }

    @Test
    public void log_reportsThrowableToFirebase_whenThrowableIsNonNull() {
        Throwable expected = new Throwable();
        int dummyPriority = 1;
        firebaseReleaseTree.log(dummyPriority, "dummyTag", "dummyMsg", expected);

        verify(firebaseErrorReporter).report(eq(expected));
    }
}
