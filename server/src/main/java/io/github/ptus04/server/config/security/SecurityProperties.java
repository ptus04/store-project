package io.github.ptus04.server.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "custom.security")
@Getter
@Setter
public class SecurityProperties {
    private List<String> allowedOrigins = List.of();
    private List<String> allowedMethods = List.of();
    private List<String> allowedHeaders = List.of();
    private boolean allowCredentials = false;
}
