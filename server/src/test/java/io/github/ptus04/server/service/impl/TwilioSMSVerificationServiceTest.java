package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.config.twilio.TwilioConfig;
import io.github.ptus04.server.service.SMSVerificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
        TwilioConfig.class,
        TwilioSMSVerificationService.class
})
@ActiveProfiles("test")
class TwilioSMSVerificationServiceTest {
    @Autowired
    private SMSVerificationService smsVerificationService;

    @Test
    void sendVerificationCode() {
        String sid = smsVerificationService.sendVerificationCode("<PHONE_NUMBER>");
        Assertions.assertNotNull(sid);
    }

    @Test
    void verifyCode() {
        boolean status = smsVerificationService.verifyCode("<PHONE_NUMBER>", "<VERIFICATION_CODE>");
        Assertions.assertTrue(status);
    }
}
