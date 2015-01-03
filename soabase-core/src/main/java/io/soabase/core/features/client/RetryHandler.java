/**
 * Copyright 2014 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.core.features.client;

public interface RetryHandler
{
    /**
     * Return true if the given arguments require a retry
     *
     * @param retryContext the retry context
     * @param retryCount 0 based retry count
     * @param statusCode the response status code or 0
     * @param exception any exception (might be null)
     */
    public boolean shouldBeRetried(RetryContext retryContext, int retryCount, int statusCode, Throwable exception);
}
