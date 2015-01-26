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
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class SimpleAuthMethod implements AuthMethod
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final List<User> validUsers;

    public static class User
    {
        private final String username;
        private final String password;

        public User(String username, String password)
        {
            this.username = Preconditions.checkNotNull(username, "username cannot be null");
            this.password = Preconditions.checkNotNull(password, "password cannot be null");
        }

        @Override
        public boolean equals(Object o)
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            User user = (User)o;

            if ( !password.equals(user.password) )
            {
                return false;
            }
            //noinspection RedundantIfStatement
            if ( !username.equals(user.username) )
            {
                return false;
            }

            return true;
        }

        public String getUsername()
        {
            return username;
        }

        @Override
        public int hashCode()
        {
            int result = username.hashCode();
            result = 31 * result + password.hashCode();
            return result;
        }
    }

    public SimpleAuthMethod(List<User> validUsers)
    {
        validUsers = Preconditions.checkNotNull(validUsers, "validUsers cannot be null");
        this.validUsers = ImmutableList.copyOf(validUsers);
    }

    @Override
    public boolean login(HttpServletRequest request, String username, String email, String domain, String password) throws Exception
    {
        User findUser = new User(username, password);
        if ( validUsers.contains(findUser) )
        {
            internalSetLogin(request, findUser.username);
            return true;
        }
        return false;
    }

    @Override
    public void logOut(HttpServletRequest request) throws Exception
    {
        HttpSession session = request.getSession();
        if ( session != null )
        {
            session.removeAttribute(SimpleAuthMethod.class.getName());
        }
    }

    @Override
    public AuthDetails requestIsAuthorized(HttpServletRequest request) throws Exception
    {
        HttpSession session = request.getSession();
        if ( session != null )
        {
            Object name = session.getAttribute(SimpleAuthMethod.class.getName());
            if ( name != null )
            {
                return new AuthDetails(true, String.valueOf(name));
            }
        }
        return new AuthDetails(false, "");
    }

    protected SimpleAuthMethod()
    {
        this.validUsers = ImmutableList.of();
    }

    protected void internalSetLogin(HttpServletRequest request, String name)
    {
        log.info("Logged in as " + name);

        HttpSession session = request.getSession(true);
        session.setAttribute(SimpleAuthMethod.class.getName(), name);
    }
}
