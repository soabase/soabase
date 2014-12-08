package io.soabase.example.goodbye;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/goodbye")
public class GoodbyeResource
{
    @GET
    public String getGoodbye() throws Exception
    {
        return "goodbye";
    }
}
