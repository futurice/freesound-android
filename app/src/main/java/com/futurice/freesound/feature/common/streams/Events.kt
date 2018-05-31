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

package com.futurice.freesound.feature.common.streams

sealed class Fetch<T> {
    class InProgress<T> : Fetch<T>()
    data class Success<T>(val value: T) : Fetch<T>()
    class Failure<T>(val error: Throwable) : Fetch<T>()
}

sealed class Operation {
    object InProgress : Operation()
    object Complete : Operation()
    class Failure(val error: Throwable) : Operation()
}
