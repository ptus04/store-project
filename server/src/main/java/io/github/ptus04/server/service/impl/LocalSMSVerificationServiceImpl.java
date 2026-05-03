package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.service.SMSVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalSMSVerificationServiceImpl implements SMSVerificationService {
    private final RandomGenerator randomGenerator = new SecureRandom();
    private final StringRedisTemplate redisTemplate;

    @Override
    public long sendOtp(String phone) {
        String key = "twilio:otp:" + phone;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            int code = randomGenerator.nextInt(0, 1_000_000);
            redisTemplate.opsForValue().setIfAbsent(key, Integer.toString(code));
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
            log.atInfo()
                    .setMessage("Phone: {}; OTP: {}")
                    .addArgument(phone)
                    .addArgument(String.format("%06d", code))
                    .log();
        }

        return redisTemplate.getExpire(key);
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        String key = "twilio:otp:" + phone;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            log.atWarn()
                    .setMessage("OTP belongs to {} is not found or has expired")
                    .addArgument(phone)
                    .log();
            return false;
        }

        return otp.equals(value);
    }

}
