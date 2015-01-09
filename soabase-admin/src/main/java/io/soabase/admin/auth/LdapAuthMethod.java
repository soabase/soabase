package io.soabase.admin.auth;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Hashtable;

public class LdapAuthMethod extends SimpleAuthMethod
{
    public static final String USER_REPLACEMENT = "$USER$";
    public static final String EMAIL_REPLACEMENT = "$EMAIL$";
    public static final String DOMAIN_REPLACEMENT = "$DOMAIN$";

    private final String authenticationType;
    private final String query;
    private final URI ldapUri;

    public LdapAuthMethod(URI ldapUri, String query)
    {
        this(ldapUri, "simple", query);
    }

    public LdapAuthMethod(URI ldapUri, String authenticationType, String query)
    {
        this.ldapUri = ldapUri;
        this.authenticationType = authenticationType;
        this.query = query;
    }

    @Override
    public boolean login(HttpServletRequest request, String username, String email, String domain, String password) throws Exception
    {
        // from http://docs.oracle.com/javase/jndi/tutorial/ldap/security/ldap.html

        String localQuery = query.replace(USER_REPLACEMENT, sanitize(username));
        localQuery = localQuery.replace(EMAIL_REPLACEMENT, sanitize(email));
        localQuery = localQuery.replace(DOMAIN_REPLACEMENT, sanitize(domain));

        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUri.toString());
        env.put(Context.SECURITY_AUTHENTICATION, authenticationType);
        env.put(Context.SECURITY_PRINCIPAL, localQuery);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        try
        {
            ctx = new InitialDirContext(env);
            internalSetLogin(request, username);
            return true;
        }
        catch ( NamingException e )
        {
            // TODO logging
            e.printStackTrace();
        }
        finally
        {
            if ( ctx != null )
            {
                ctx.close();
            }
        }

        return false;
    }

    // from http://blog.dzhuvinov.com/?p=585
    /**
     * Escapes any special chars (RFC 4515) from a string representing a
     * a search filter assertion value.
     *
     * @param input The input string.
     * @return A assertion value string ready for insertion into a
     * search filter string.
     */
    public static String sanitize(final String input)
    {
        if ( input == null )
        {
            return "";
        }

        String s = "";

        for ( int i = 0; i < input.length(); i++ )
        {

            char c = input.charAt(i);

            if ( c == '*' )
            {
                // escape asterisk
                s += "\\2a";
            }
            else if ( c == '(' )
            {
                // escape left parenthesis
                s += "\\28";
            }
            else if ( c == ')' )
            {
                // escape right parenthesis
                s += "\\29";
            }
            else if ( c == '\\' )
            {
                // escape backslash
                s += "\\5c";
            }
            else if ( c == '\u0000' )
            {
                // escape NULL char
                s += "\\00";
            }
            else if ( c <= 0x7f )
            {
                // regular 1-byte UTF-8 char
                s += String.valueOf(c);
            }
            else if ( c >= 0x080 )
            {

                // higher-order 2, 3 and 4-byte UTF-8 chars

                try
                {
                    byte[] utf8bytes = String.valueOf(c).getBytes("UTF8");

                    for ( byte b : utf8bytes )
                    {
                        s += String.format("\\%02x", b);
                    }

                }
                catch ( UnsupportedEncodingException e )
                {
                    // ignore
                }
            }
        }

        return s;
    }
}
