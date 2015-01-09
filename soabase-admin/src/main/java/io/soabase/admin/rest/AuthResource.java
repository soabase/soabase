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
package io.soabase.admin.rest;

import io.soabase.admin.auth.AuthDetails;
import io.soabase.admin.auth.AuthSpecHolder;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/soa/auth")
public class AuthResource
{
    private final AuthSpecHolder authSpecHolder;

    @Inject
    public AuthResource(AuthSpecHolder authSpecHolder)
    {
        this.authSpecHolder = authSpecHolder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authorize(@Context HttpServletRequest request, @FormParam("username") String username, @FormParam("email") String email, @FormParam("domain") String domain, @FormParam("password") String password) throws Exception
    {
        if ( !authSpecHolder.hasAuth() )
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if ( authSpecHolder.getAuthSpec().getAuthMethod().login(request, username, email, domain, password) )
        {
            return Response.temporaryRedirect(new URI("/")).build();
        }
        return Response.temporaryRedirect(new URI("/signin?error=true")).build();
    }

    @DELETE
    public Response unAuthorize(@Context HttpServletRequest request) throws Exception
    {
        if ( !authSpecHolder.hasAuth() )
        {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        authSpecHolder.getAuthSpec().getAuthMethod().logOut(request);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AuthStatus getAuthStatus(@Context HttpServletRequest request) throws Exception
    {
        AuthStatus status = new AuthStatus();
        if ( authSpecHolder.hasAuth() )
        {
            AuthDetails authDetails = authSpecHolder.getAuthSpec().getAuthMethod().requestIsAuthorized(request);
            status.setName(authDetails.isAuthorized() ? authDetails.getAuthName() : "");
            status.setType(authDetails.isAuthorized() ? AuthStatusType.LOGGED_IN : AuthStatusType.MUST_LOG_IN);
        }
        else
        {
            status.setName("");
            status.setType(AuthStatusType.NOT_LOGGED_IN);
        }
        return status;
    }
}
