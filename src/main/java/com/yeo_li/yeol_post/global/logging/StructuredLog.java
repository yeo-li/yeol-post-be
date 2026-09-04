package com.yeo_li.yeol_post.global.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.MDC;

public final class StructuredLog {

    public static final String REQUEST_ID_HEADER = "x-request-id";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private StructuredLog() {
    }

    public static Builder event(String event, String message, String reason) {
        return new Builder(event, message, reason);
    }

    public static final class Builder {

        private final Map<String, Object> fields = new LinkedHashMap<>();

        private Builder(String event, String message, String reason) {
            fields.put("message", message);
            fields.put("event", event);
            fields.put("reason", reason);

            String requestId = MDC.get(REQUEST_ID_MDC_KEY);
            if (requestId != null && !requestId.isBlank()) {
                fields.put("requestId", requestId);
            }
        }

        public Builder field(String key, Object value) {
            if (value != null) {
                fields.put(key, value);
            }
            return this;
        }

        public Builder throwable(Throwable throwable) {
            if (throwable == null) {
                return this;
            }

            fields.put("exceptionType", throwable.getClass().getName());
            fields.put("exceptionMessage", throwable.getMessage());
            fields.put("stackTrace", stackTraceOf(throwable));
            return this;
        }

        public String build() {
            try {
                return OBJECT_MAPPER.writeValueAsString(fields);
            } catch (JsonProcessingException e) {
                return "{\"message\":\"structured log serialization failed\","
                    + "\"event\":\"STRUCTURED_LOG_SERIALIZATION_FAILED\","
                    + "\"reason\":\"JSON_PROCESSING_FAILED\"}";
            }
        }

        private String stackTraceOf(Throwable throwable) {
            StringWriter stringWriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        }
    }
}
