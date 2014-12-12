package io.soabase.example.goodbye;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/goodbye")
public class GoodbyeAdminResource
{
    @GET
    public String get()
    {
        return "This is the admin side";
    }
}
