package com.beachape;

import io.quarkus.vertx.core.runtime.VertxMDC;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@PreMatching
@ApplicationScoped
@Provider
@Priority(Integer.MAX_VALUE)
public class ReqRespFilter implements ContainerResponseFilter, ContainerRequestFilter {
    public static final String REQUEST_METHOD_FIELD = "request.filter.field";
    public static final String RESPONSE_METHOD_FIELD = "response.filter.field";
    public static final String REQUEST_METHOD_NON_STRING_FIELD = "request.filter.non_string_field";
    public static final Map<Integer, String> REQUEST_METHOD_NON_STRING_VALUE = Map.of(1, "hello");
    public static final String REQUEST_METHOD_FIELD_VALUE = "from the request filter";
    public static final String RESPONSE_METHOD_FIELD_VALUE = "from the response filter";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        VertxMDC.INSTANCE.put(REQUEST_METHOD_FIELD, REQUEST_METHOD_FIELD_VALUE);
        VertxMDC.INSTANCE.putObject(REQUEST_METHOD_NON_STRING_FIELD, REQUEST_METHOD_NON_STRING_VALUE);
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        VertxMDC.INSTANCE.put(RESPONSE_METHOD_FIELD, RESPONSE_METHOD_FIELD_VALUE);
    }

}
