package com.markethive.MarketHive.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String method  = request.getMethod();
        String uri     = request.getRequestURI();
        String query   = request.getQueryString();
        String ip      = getClientIp(request);
        String user    = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName() : "anonymous";

        // ── Log incoming request ──────────────────────────────────────────
        log.info(">>> REQUEST  | {} {} {} | IP: {} | User: {} | Time: {}",
                method,
                uri,
                query != null ? "?" + query : "",
                ip,
                user,
                LocalDateTime.now());

        try {
            filterChain.doFilter(request, response);
        } finally {
            // ── Log outgoing response ─────────────────────────────────────
            long duration = System.currentTimeMillis() - startTime;
            int  status   = response.getStatus();

            if (status >= 500) {
                log.error("<<< RESPONSE | {} {} | STATUS: {} | DURATION: {}ms",
                        method, uri, status, duration);
            } else if (status >= 400) {
                log.warn("<<< RESPONSE | {} {} | STATUS: {} | DURATION: {}ms",
                        method, uri, status, duration);
            } else {
                log.info("<<< RESPONSE | {} {} | STATUS: {} | DURATION: {}ms",
                        method, uri, status, duration);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        // X-Forwarded-For can contain multiple IPs — take the first one
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }
}