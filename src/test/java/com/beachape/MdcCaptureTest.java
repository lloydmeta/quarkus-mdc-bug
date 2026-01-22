package com.beachape;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logmanager.LogManager;
import org.jboss.logmanager.ExtLogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to reproduce MDC propagation issue by capturing access logs in-memory
 * instead of reading from a file.
 */
@QuarkusTest
class MdcCaptureTest {


    // This test will pass on quarkusPlatformVersion=3.30.4
    @Test
    void authedEndpoint_shouldHaveExpectedMdcFields_fromRequestFilter() {
        // Make a request to trigger the filter
        given()
                .when()
                .header("Authorization", "Sleep my darling")
                .get("/v1/greeting/hello")
                .then()
                .statusCode(401);

        var mdc = getMdc();

        var requestFilterField = mdc.get(ReqRespFilter.REQUEST_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_FIELD_VALUE,
                requestFilterField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
        var requestFilterNonStringField = mdc.get(ReqRespFilter.REQUEST_METHOD_NON_STRING_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_NON_STRING_VALUE,
                requestFilterNonStringField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
    }

    @Test
    void authedEndpoint_shouldHaveExpectedMdcFields_fromResponseFilter() {
        // Make a request to trigger the filter
        given()
                .when()
                .header("Authorization", "Sleep my darling")
                .get("/v1/greeting/hello")
                .then()
                .statusCode(401);

        var mdc = getMdc();

        var responseFilterField = mdc.get(ReqRespFilter.RESPONSE_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.RESPONSE_METHOD_FIELD_VALUE,
                responseFilterField,
                "MDC field set in response filter should be available. MDC contents: " + mdc
        );
    }

    @Test
    void unauthedEndpoint_shouldHaveExpectedMdcFields_fromRequestFilter() {
        // Make a request to trigger the filter
        given()
                .when()
                .header("Authorization", "Sleep my darling")
                .get("/v1/greeting/hello-unauthed")
                .then()
                .statusCode(200);

        var mdc = getMdc();

        var requestFilterField = mdc.get(ReqRespFilter.REQUEST_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_FIELD_VALUE,
                requestFilterField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
        var requestFilterNonStringField = mdc.get(ReqRespFilter.REQUEST_METHOD_NON_STRING_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_NON_STRING_VALUE,
                requestFilterNonStringField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
    }

    @Test
    void unauthedEndpoint_shouldHaveExpectedMdcFields_fromResponseFilter() {
        // Make a request to trigger the filter
        given()
                .when()
                .header("Authorization", "Sleep my darling")
                .get("/v1/greeting/hello-unauthed")
                .then()
                .statusCode(200);

        var mdc = getMdc();

        var responseFilterField = mdc.get(ReqRespFilter.RESPONSE_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.RESPONSE_METHOD_FIELD_VALUE,
                responseFilterField,
                "MDC field set in response filter should be available. MDC contents: " + mdc
        );
    }

    @Test
    void unauthedEndpointCallingService_shouldHaveExpectedMdcFields_fromRequestFilter() {
        given()
                .when()
                .get("/v1/greeting/hello-unauthed-calling-service")
                .then()
                .statusCode(200);

        var mdc = getMdc();

        var requestFilterField = mdc.get(ReqRespFilter.REQUEST_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_FIELD_VALUE,
                requestFilterField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
        var requestFilterNonStringField = mdc.get(ReqRespFilter.REQUEST_METHOD_NON_STRING_FIELD);
        assertEquals(
                ReqRespFilter.REQUEST_METHOD_NON_STRING_VALUE,
                requestFilterNonStringField,
                "MDC field set in request filter should be available. MDC contents: " + mdc
        );
    }

    @Test
    void unauthedEndpointCallingService_shouldHaveExpectedMdcFields_fromResponseFilter() {
        given()
                .when()
                .get("/v1/greeting/hello-unauthed-calling-service")
                .then()
                .statusCode(200);

        var mdc = getMdc();

        var responseFilterField = mdc.get(ReqRespFilter.RESPONSE_METHOD_FIELD);
        assertEquals(
                ReqRespFilter.RESPONSE_METHOD_FIELD_VALUE,
                responseFilterField,
                "MDC field set in response filter should be available. MDC contents: " + mdc
        );
    }


    private static InMemoryLogHandler accessLogHandler;

    @BeforeAll
    static void setup() {
        // Register handler to capture access logs
        accessLogHandler =
                new InMemoryLogHandler(record -> {
                    var logger = record.getLoggerName();
                    return logger != null && logger.equals("io.quarkus.http.access-log");
                });

        var rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.addHandler(accessLogHandler);
    }

    @AfterEach
    void clearLogs() {
        accessLogHandler.clear();
    }

    private Map<String, Object> getMdc() {
        // Get captured access log records
        var logs = accessLogHandler.getRecords();
        assertFalse(logs.isEmpty(), "Access log should have been captured");

        ExtLogRecord log = logs.getFirst();
        log.copyMdc();

        try {
            Field mdcField = ExtLogRecord.class.getDeclaredField("mdcCopy");
            mdcField.setAccessible(true);
            Object mdcObj = mdcField.get(log);
            if (mdcObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) mdcObj;
                return result;
            } else {
                throw new IllegalStateException("mdcCopy field is not a Map, it's a " + mdcObj.getClass().getName());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to access mdcCopy field on ExtLogRecord", e);
        }
    }
}
