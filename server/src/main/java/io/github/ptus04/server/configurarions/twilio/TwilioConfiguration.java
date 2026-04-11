package io.github.ptus04.server.configurarions.twilio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twilio")
@Getter
@Setter
public class TwilioConfiguration {
    private String verifyServiceSid;
    private String accountSid;
    private String authToken;
}
