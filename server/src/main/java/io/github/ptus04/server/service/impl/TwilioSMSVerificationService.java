package io.github.ptus04.server.service.impl;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import io.github.ptus04.server.config.TwilioProperties;
import io.github.ptus04.server.service.SMSVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Primary
@ConditionalOnBean(TwilioProperties.class)
@RequiredArgsConstructor
@Service
public class TwilioSMSVerificationService implements SMSVerificationService {
    private final TwilioProperties twilioProperties;
    private final StringRedisTemplate redisTemplate;

    @Override
    public long sendOtp(String phone) {
        String key = "twilio:otp:" + phone;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            Verification.creator(
                    twilioProperties.getVerifyServiceSid(),
                    prefixWithVietnameseCode(phone),
                    "sms"
            ).create();
            redisTemplate.opsForValue().setIfAbsent(key, "sent");
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }

        return redisTemplate.getExpire(key);
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        VerificationCheck check = VerificationCheck
                .creator(twilioProperties.getVerifyServiceSid())
                .setCode(otp)
                .setTo(prefixWithVietnameseCode(phone))
                .create();

        return "approved".equals(check.getStatus());
    }

    private String prefixWithVietnameseCode(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
