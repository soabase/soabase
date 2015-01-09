package io.soabase.admin.auth;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class SimpleAuthMethod implements AuthMethod
{
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
            HttpSession session = request.getSession(true);
            session.setAttribute(SimpleAuthMethod.class.getName(), findUser.username);
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
}
