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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Primary
@ConditionalOnBean(TwilioProperties.class)
@RequiredArgsConstructor
@Service
public class TwilioSMSVerificationServiceImpl implements SMSVerificationService {
    private final TwilioProperties twilioProperties;
    private final StringRedisTemplate redisTemplate;

    @Override
    public long sendOtp(String phone) {
        String key = "twilio:otp:" + phone;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            Verification.creator(
                    twilioProperties.getVerify().getServiceSid(),
                    prefixWithVietnameseCode(phone),
                    Verification.Channel.SMS.toString()
            ).create();
            redisTemplate.opsForValue().setIfAbsent(key, "sent");
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }

        return redisTemplate.getExpire(key);
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        VerificationCheck check = VerificationCheck
                .creator(twilioProperties.getVerify().getServiceSid())
                .setCode(otp)
                .setTo(prefixWithVietnameseCode(phone))
                .create();

        return Objects.equals(check.getStatus(), Verification.Status.APPROVED.toString());
    }

    private String prefixWithVietnameseCode(String phoneNumber) {
        return phoneNumber.startsWith("0") ? "+84" + phoneNumber.substring(1) : phoneNumber;
    }
}
