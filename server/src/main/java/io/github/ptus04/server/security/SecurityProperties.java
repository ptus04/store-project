package io.github.ptus04.server.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "custom.security")
public class SecurityProperties {
    @NestedConfigurationProperty
    private final CorsProperties cors = new CorsProperties();

    @Getter
    @Setter
    public static final class CorsProperties {
        private List<String> allowedOrigins = List.of();
        private List<String> allowedMethods = List.of();
        private List<String> allowedHeaders = List.of();
        private boolean allowCredentials = false;
    }
}
