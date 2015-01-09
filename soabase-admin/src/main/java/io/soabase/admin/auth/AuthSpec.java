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
package io.soabase.admin.auth;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class AuthSpec
{
    private final Set<AuthFields> fields;
    private final int signInSslPort;
    private final String signInHeading;
    private final String signInButton;
    private final AuthMethod authMethod;

    public AuthSpec(AuthMethod authMethod, Set<AuthFields> fields)
    {
        this(authMethod, fields, 0);
    }

    public AuthSpec(AuthMethod authMethod, Set<AuthFields> fields, int signInSslPort)
    {
        this(authMethod, fields, signInSslPort, "Please Sign In", "Sign In");
    }

    public AuthSpec(AuthMethod authMethod, Set<AuthFields> fields, int signInSslPort, String signInHeading, String signInButton)
    {
        fields = Preconditions.checkNotNull(fields, "fields cannot be null");
        this.authMethod = Preconditions.checkNotNull(authMethod, "authMethod cannot be null");
        this.fields = ImmutableSet.copyOf(fields);
        this.signInSslPort = signInSslPort;
        this.signInHeading = Preconditions.checkNotNull(signInHeading, "signInHeading cannot be null");
        this.signInButton = Preconditions.checkNotNull(signInButton, "signInButton cannot be null");
    }

    public AuthMethod getAuthMethod()
    {
        return authMethod;
    }

    public Set<AuthFields> getFields()
    {
        return fields;
    }

    public int getSignInSslPort()
    {
        return signInSslPort;
    }

    public String getSignInHeading()
    {
        return signInHeading;
    }

    public String getSignInButton()
    {
        return signInButton;
    }
}
