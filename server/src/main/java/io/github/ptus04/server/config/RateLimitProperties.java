package io.github.ptus04.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "custom.rate-limit")
public class RateLimitProperties {
    private boolean enabled = true;
    private int capacity = 120;
    private Duration window = Duration.ofMinutes(1);
    private List<Policy> policies = new ArrayList<>();

    @Data
    public static class Policy {
        private String pathPrefix;
        private int capacity;
        private Duration window;
    }
}
