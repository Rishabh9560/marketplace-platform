package com.logicveda.marketplace.vendor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Request logging interceptor
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String TRACE_ID_ATTRIBUTE = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        log.info("Incoming request - TraceID: {}, Method: {}, URI: {}, IP: {}",
            traceId, request.getMethod(), request.getRequestURI(), getClientIP(request));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        String traceId = (String) request.getAttribute(TRACE_ID_ATTRIBUTE);
        
        log.info("Request completed - TraceID: {}, Status: {}, Duration: {} ms",
            traceId, response.getStatus(), getTiming(request));

        if (ex != null) {
            log.error("Request error - TraceID: {}, Error: {}", traceId, ex.getMessage(), ex);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private long getTiming(HttpServletRequest request) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime == null) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
}
