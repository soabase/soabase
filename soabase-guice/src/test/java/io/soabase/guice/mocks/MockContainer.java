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
package io.soabase.guice.mocks;

import javax.inject.Inject;

public class MockContainer
{
    private final MockGuiceInjected guiceInjected;
    private final MockHK2Injected hk2Injected;

    @Inject
    public MockContainer(MockGuiceInjected guiceInjected, MockHK2Injected hk2Injected)
    {
        this.guiceInjected = guiceInjected;
        this.hk2Injected = hk2Injected;
    }

    public MockGuiceInjected getGuiceInjected()
    {
        return guiceInjected;
    }

    public MockHK2Injected getHk2Injected()
    {
        return hk2Injected;
    }
}
