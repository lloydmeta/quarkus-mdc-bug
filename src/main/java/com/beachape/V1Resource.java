package com.beachape;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Default
@ApplicationScoped
@RunOnVirtualThread
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class V1Resource {

    @Inject GreetingResource greetingResource;


    @Path("/greeting")
    public GreetingResource getGreetingResource() {
        return greetingResource;
    }
}
