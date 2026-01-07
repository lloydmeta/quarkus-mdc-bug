package com.beachape;

import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@RunOnVirtualThread
@Default
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GreetingResource {

    static private Logger LOGGER = Logger.getLogger(GreetingResource.class);

    @Inject
    GreetingService greetingService;

    @Authenticated
    @GET
    @Path("/hello")
    public String hello() {
        LOGGER.error("in hello handler");
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/hello-unauthed")
    public String helloUnauthed() {
        LOGGER.error("in helloUnauthed handler");
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/hello-unauthed-calling-service")
    public String helloUnauthedCallingService() {
        LOGGER.error("in helloUnauthedCallingService handler");
        return greetingService.processGreeting();
    }
}
