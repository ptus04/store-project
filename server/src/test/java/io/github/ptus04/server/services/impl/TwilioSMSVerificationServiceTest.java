package io.github.ptus04.server.services.impl;

import io.github.ptus04.server.configurarions.twilio.TwilioSdkInitializer;
import io.github.ptus04.server.services.SMSVerificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
        TwilioSdkInitializer.class,
        TwilioSMSVerificationService.class
})
@ActiveProfiles("test")
class TwilioSMSVerificationServiceTest {
    @Autowired
    private SMSVerificationService smsVerificationService;

    @Test
    void sendVerificationCode() {
        String sid = smsVerificationService.sendVerificationCode("+84868635209");
        Assertions.assertNotNull(sid);
    }

    @Test
    void verifyCode() {
        boolean status = smsVerificationService.verifyCode("+84868635209", "147193");
        Assertions.assertTrue(status);
    }
}
