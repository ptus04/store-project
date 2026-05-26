package io.github.ptus04.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ptus04.server.config.RateLimitProperties;
import io.github.ptus04.server.dto.response.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final String KEY_PREFIX = "rate_limit:";

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !properties.isEnabled() || !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        RateLimitPolicy policy = resolvePolicy(request.getRequestURI());
        String key = buildKey(request, policy);
        Long requestCount;

        try {
            requestCount = redisTemplate.opsForValue().increment(key);

            if (requestCount != null && requestCount == 1) {
                redisTemplate.expire(key, policy.window());
            }
        } catch (DataAccessException ex) {
            log.warn("Skipping rate limit because Redis is unavailable.", ex);
            filterChain.doFilter(request, response);
            return;
        }

        long ttlSeconds = Optional.ofNullable(redisTemplate.getExpire(key))
                .filter(ttl -> ttl > 0)
                .orElse(policy.window().toSeconds());

        response.setHeader("X-RateLimit-Limit", String.valueOf(policy.capacity()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, policy.capacity() - safeCount(requestCount))));
        response.setHeader("X-RateLimit-Reset", String.valueOf(ttlSeconds));

        if (requestCount != null && requestCount > policy.capacity()) {
            writeTooManyRequestsResponse(request, response, ttlSeconds);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitPolicy resolvePolicy(String path) {
        return properties.getPolicies()
                .stream()
                .filter(policy -> policy.getPathPrefix() != null && path.startsWith(policy.getPathPrefix()))
                .findFirst()
                .map(policy -> new RateLimitPolicy(
                        policy.getPathPrefix(),
                        policy.getCapacity() > 0 ? policy.getCapacity() : properties.getCapacity(),
                        policy.getWindow() != null ? policy.getWindow() : properties.getWindow()
                ))
                .orElseGet(() -> new RateLimitPolicy("default", properties.getCapacity(), properties.getWindow()));
    }

    private String buildKey(HttpServletRequest request, RateLimitPolicy policy) {
        return KEY_PREFIX + clientIp(request) + ":" + request.getMethod() + ":" + policy.name();
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private long safeCount(Long requestCount) {
        return requestCount == null ? 0 : requestCount;
    }

    private void writeTooManyRequestsResponse(HttpServletRequest request, HttpServletResponse response, long ttlSeconds)
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(ttlSeconds));

        ApiErrorResponse<?> body = new ApiErrorResponse<>(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many requests. Please try again later.",
                request.getRequestURI(),
                "RATE_LIMIT_EXCEEDED"
        );
        objectMapper.writeValue(response.getWriter(), body);
    }

    private record RateLimitPolicy(String name, int capacity, Duration window) {
    }
}
