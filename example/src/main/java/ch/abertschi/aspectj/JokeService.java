package ch.abertschi.aspectj;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.ejb.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("joke")
@Stateless
public class JokeService
{
    private static final String JOKE_URL = "http://api.icndb.com/jokes/random";

    @Interceptor
    @GET
    @Produces("text/plain")
    public Response tell()
    {
        try
        {
            HttpResponse<JsonNode> joke = Unirest.get(JOKE_URL)
                    .header("accept", "application/json")
                    .asJson();
            if (joke.getStatus() == 200)
            {
                String msg = joke.getBody().getObject().getJSONObject("value").get("joke").toString();
                return Response.ok(msg).build();
            }
            else
            {
                return Response.status(500).build();
            }
        }
        catch (UnirestException e)
        {
            return Response.serverError().build();
        }
    }
}
