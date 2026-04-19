package io.github.ptus04.server.service.impl;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import io.github.ptus04.server.config.twilio.TwilioProperties;
import io.github.ptus04.server.service.SMSVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwilioSMSVerificationService implements SMSVerificationService {
    private final TwilioProperties twilioProperties;

    @Override
    public String sendVerificationCode(String phoneNumber) {
        Verification verification = Verification.creator(
                twilioProperties.getVerifyServiceSid(),
                phoneNumber,
                "sms"
        ).create();

        return verification.getSid();
    }

    @Override
    public boolean verifyCode(String phoneNumber, String verificationCode) {
        VerificationCheck check = VerificationCheck.creator(twilioProperties.getVerifyServiceSid())
                .setCode(verificationCode)
                .setTo(phoneNumber)
                .create();

        return "approved".equals(check.getStatus());
    }
}
