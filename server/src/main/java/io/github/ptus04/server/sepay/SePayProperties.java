package io.github.ptus04.server.sepay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.sepay")
@Getter
@Setter
public class SePayProperties {
    private String accountNumber = "";
    private String accountName = "";
    private String bank = "";
    private String username = "";
    private String password = "";
}
