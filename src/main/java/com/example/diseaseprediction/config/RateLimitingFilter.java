package com.example.diseaseprediction.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate limiting filter to prevent API abuse.
 * Uses a sliding window algorithm with configurable limits.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter implements Filter {

    @Value("${app.ratelimit.enabled:true}")
    private boolean enabled;

    @Value("${app.ratelimit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${app.ratelimit.burst-capacity:100}")
    private int burstCapacity;

    // Store request counts per IP
    private final Map<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();

    // Cleanup interval (5 minutes)
    private static final long CLEANUP_INTERVAL_MS = 5 * 60 * 1000;
    private long lastCleanup = System.currentTimeMillis();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip rate limiting for static resources
        String path = httpRequest.getRequestURI();
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(httpRequest);
        
        // Periodic cleanup of old entries
        cleanupIfNeeded();

        RateLimitInfo rateLimitInfo = requestCounts.computeIfAbsent(clientIp, k -> new RateLimitInfo());
        
        if (rateLimitInfo.isRateLimited(requestsPerMinute, burstCapacity)) {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            httpResponse.setHeader("X-RateLimit-Remaining", "0");
            httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitInfo.getResetTimeSeconds()));
            httpResponse.setHeader("Retry-After", String.valueOf(rateLimitInfo.getRetryAfterSeconds()));
            
            httpResponse.getWriter().write(
                "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Please try again later.\",\"retryAfter\":" 
                + rateLimitInfo.getRetryAfterSeconds() + "}"
            );
            return;
        }

        // Add rate limit headers
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitInfo.getRemainingRequests(requestsPerMinute)));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitInfo.getResetTimeSeconds()));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        requestCounts.clear();
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".html") || 
               path.endsWith(".css") || 
               path.endsWith(".js") || 
               path.endsWith(".png") || 
               path.endsWith(".jpg") || 
               path.endsWith(".svg") || 
               path.endsWith(".ico") ||
               path.endsWith(".woff2") ||
               path.startsWith("/icons/") ||
               path.equals("/manifest.json") ||
               path.equals("/sw.js");
    }

    private String getClientIp(HttpServletRequest request) {
        // Check for forwarded headers (when behind proxy/load balancer)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private void cleanupIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup > CLEANUP_INTERVAL_MS) {
            lastCleanup = now;
            // Remove entries older than 2 minutes
            long cutoff = now - (2 * 60 * 1000);
            requestCounts.entrySet().removeIf(entry -> entry.getValue().getLastRequestTime() < cutoff);
        }
    }

    /**
     * Tracks rate limit info for a single client IP.
     */
    private static class RateLimitInfo {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong lastRequestTime = new AtomicLong(System.currentTimeMillis());

        public boolean isRateLimited(int maxRequests, int burstCapacity) {
            long now = System.currentTimeMillis();
            lastRequestTime.set(now);
            
            // Reset window if a minute has passed
            if (now - windowStart.get() > 60_000) {
                windowStart.set(now);
                requestCount.set(0);
            }

            int currentCount = requestCount.incrementAndGet();
            
            // Allow burst capacity
            return currentCount > burstCapacity;
        }

        public int getRemainingRequests(int maxRequests) {
            int remaining = maxRequests - requestCount.get();
            return Math.max(0, remaining);
        }

        public long getResetTimeSeconds() {
            long resetTime = windowStart.get() + 60_000;
            return resetTime / 1000;
        }

        public int getRetryAfterSeconds() {
            long remaining = (windowStart.get() + 60_000) - System.currentTimeMillis();
            return (int) Math.max(1, remaining / 1000);
        }

        public long getLastRequestTime() {
            return lastRequestTime.get();
        }
    }
}
