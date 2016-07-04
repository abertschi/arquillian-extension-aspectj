package ch.abertschi.aspectj;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.ejb.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("joke")
@Stateless
@Local
public class JokeService
{
    private static final String JOKE_URL = "http://api.icndb.com/jokes/random";

    @Interceptor
    @GET
    @Produces("text/plain")
    public String tell()
    {
        try
        {
            HttpResponse<JsonNode> joke = Unirest.get(JOKE_URL)
                    .header("accept", "application/json")
                    .asJson();
            if (joke.getStatus() == 200)
            {
                return joke.getBody().getObject().getJSONObject("value").get("joke").toString();
            }
            else
            {
                throw new RuntimeException("Can't talk to joke api");
            }
        }
        catch (UnirestException e)
        {
            throw new RuntimeException("Can't talk to joke api");
        }
    }
}
