package io.github.ptus04.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sepay.qr")
public class SePayQRProperties {
    private String bank = "";
    private String accountNumber = "";
    private String accountName = "";
}
