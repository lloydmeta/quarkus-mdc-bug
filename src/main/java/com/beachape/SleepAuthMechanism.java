package com.beachape;


import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.text.MessageFormat;

@ApplicationScoped
@Priority(3)
public class SleepAuthMechanism implements HttpAuthenticationMechanism {

    private static final Logger LOGGER = Logger.getLogger(SleepAuthMechanism.class);
    private static final String SLEEP_PREFIX = "Sleep ";

    @Override
    @WithSpan // Comment this out and it will also pass
    public Uni<SecurityIdentity> authenticate(
            RoutingContext context, IdentityProviderManager identityProviderManager) {
        LOGGER.error("in sleep auth mechanism");
        String authHeader = context.request().getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(SLEEP_PREFIX)) {
            return Uni.createFrom().nullItem();
        }

        LOGGER.error("authing without sleep");

        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        var challenge =
                new ChallengeData(
                        Response.Status.UNAUTHORIZED.getStatusCode(),
                        HttpHeaders.WWW_AUTHENTICATE,
                        MessageFormat.format("{0}realm=\"quarkus-mdc-bug\"", SLEEP_PREFIX));

        return Uni.createFrom().item(challenge);
    }
}

