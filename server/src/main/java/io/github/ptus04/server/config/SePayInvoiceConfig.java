package io.github.ptus04.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SePayInvoiceProperties.class)
public class SePayInvoiceConfig {
}
