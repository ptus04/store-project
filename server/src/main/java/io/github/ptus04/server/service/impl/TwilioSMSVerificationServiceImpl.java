package io.github.ptus04.server.service.impl;

import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import io.github.ptus04.server.config.TwilioProperties;
import io.github.ptus04.server.service.SMSVerificationService;
import io.github.ptus04.server.util.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
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
        Long expire = redisTemplate.getExpire(key);
        if (expire != null && expire > 0) {
            return expire;
        }

        redisTemplate.opsForValue().set(key, "sent", 60, TimeUnit.SECONDS);

        Verification.creator(
                twilioProperties.getVerify().getServiceSid(),
                PhoneNumberUtils.prefixWithVietnameseCode(phone),
                Verification.Channel.SMS.toString()
        ).create();

        return 60;
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        VerificationCheck check = VerificationCheck
                .creator(twilioProperties.getVerify().getServiceSid())
                .setCode(otp)
                .setTo(PhoneNumberUtils.prefixWithVietnameseCode(phone))
                .create();

        return Objects.equals(check.getStatus(), Verification.Status.APPROVED.toString());
    }
}
