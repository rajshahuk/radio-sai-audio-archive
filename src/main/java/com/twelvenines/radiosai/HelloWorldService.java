package com.twelvenines.radiosai;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by raj on 21/07/2017.
 */
@Path("helloWorld")
public class HelloWorldService {
    @GET
    @Produces("text/plain")
    public String get() {
        return "Hello, World";
    }

}
