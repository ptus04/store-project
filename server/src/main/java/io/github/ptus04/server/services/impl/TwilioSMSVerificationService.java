package io.github.ptus04.server.services.impl;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import io.github.ptus04.server.configurarions.twilio.TwilioConfiguration;
import io.github.ptus04.server.services.SMSVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwilioSMSVerificationService implements SMSVerificationService {
    private final TwilioConfiguration twilioConfiguration;

    @Override
    public String sendVerificationCode(String phoneNumber) {
        Verification verification = Verification.creator(
                twilioConfiguration.getVerifyServiceSid(),
                phoneNumber,
                "sms"
        ).create();

        return verification.getSid();
    }

    @Override
    public boolean verifyCode(String phoneNumber, String verificationCode) {
        VerificationCheck check = VerificationCheck.creator(twilioConfiguration.getVerifyServiceSid())
                .setCode(verificationCode)
                .setTo(phoneNumber)
                .create();

        return "approved".equals(check.getStatus());
    }
}
