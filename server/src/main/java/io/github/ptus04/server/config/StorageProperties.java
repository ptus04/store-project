package io.github.ptus04.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.storage")
@Getter
@Setter
public class StorageProperties {
    private String url;
}
