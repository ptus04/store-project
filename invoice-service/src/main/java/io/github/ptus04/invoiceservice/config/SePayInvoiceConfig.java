package io.github.ptus04.invoiceservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SePayInvoiceProperties.class)
public class SePayInvoiceConfig {
}
