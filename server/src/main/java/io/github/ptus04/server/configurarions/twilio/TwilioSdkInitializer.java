package io.github.ptus04.server.configurarions.twilio;

import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TwilioConfiguration.class)
@RequiredArgsConstructor
public class TwilioSdkInitializer implements ApplicationRunner {
    private final TwilioConfiguration twilioConfiguration;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        Twilio.init(twilioConfiguration.getAccountSid(), twilioConfiguration.getAuthToken());
    }
}
