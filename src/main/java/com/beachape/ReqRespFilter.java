package com.beachape;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logmanager.MDC;

@PreMatching
@ApplicationScoped
@Provider
@Priority(Integer.MAX_VALUE)
public class ReqRespFilter implements ContainerResponseFilter, ContainerRequestFilter {
    public static final String REQUEST_METHOD_FIELD = "request.filter.field";
    public static final String RESPONSE_METHOD_FIELD = "response.filter.field";
    public static final String REQUEST_METHOD_FIELD_VALUE = "from the request filter";
    public static final String RESPONSE_METHOD_FIELD_VALUE = "from the response filter";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        MDC.put(REQUEST_METHOD_FIELD, REQUEST_METHOD_FIELD_VALUE);
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        MDC.put(RESPONSE_METHOD_FIELD, RESPONSE_METHOD_FIELD_VALUE);
    }

}
