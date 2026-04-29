package io.github.ptus04.server.config;

import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"prod", "test"})
@Configuration
@EnableConfigurationProperties(TwilioProperties.class)
@RequiredArgsConstructor
public class TwilioConfig implements ApplicationRunner {
    private final TwilioProperties twilioProperties;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
    }
}
