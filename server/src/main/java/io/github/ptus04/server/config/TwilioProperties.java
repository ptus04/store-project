package io.github.ptus04.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.twilio")
@Getter
@Setter
public class TwilioProperties {
    private String verifyServiceSid;
    private String accountSid;
    private String authToken;
}
