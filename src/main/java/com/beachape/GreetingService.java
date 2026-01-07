package com.beachape;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Duration;

@ApplicationScoped
public class GreetingService {

    private static final Logger LOGGER = Logger.getLogger(GreetingService.class);

    public String processGreeting() {
        LOGGER.error("in greeting service with @WithSpan");

        try {
            Thread.sleep(Duration.ofMillis(10));
        } catch (InterruptedException e) {
            LOGGER.error("Sleep interrupted", e);
        }

        return "Hello from service with @WithSpan";
    }
}
