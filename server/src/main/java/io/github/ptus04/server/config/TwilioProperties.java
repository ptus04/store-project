package io.github.ptus04.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@Setter
@ConfigurationProperties(prefix = "twilio")
public class TwilioProperties {
    private String accountSid = "";
    private String authToken = "";
    @NestedConfigurationProperty
    private VerifyProperties verify = new VerifyProperties();

    @Getter
    @Setter
    public static class VerifyProperties {
        private String serviceSid = "";
    }
}
