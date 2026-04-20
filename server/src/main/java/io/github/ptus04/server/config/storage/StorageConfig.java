package io.github.ptus04.server.config.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@RequiredArgsConstructor
public class StorageConfig {

}
