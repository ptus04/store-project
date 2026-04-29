package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.service.SMSVerificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class LocalSMSVerificationServiceImpl implements SMSVerificationService {
    private final Logger logger = LoggerFactory.getLogger(LocalSMSVerificationServiceImpl.class);
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
            logger.info("Phone: {}; OTP: {}", phone, String.format("%06d", code));
        }

        return redisTemplate.getExpire(key);
    }

    @Override
    public boolean verifyOtp(String phone, String otp) {
        String key = "twilio:otp:" + phone;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            logger.warn("OTP belongs to {} is not found or has expired", phone);
            return false;
        }

        return otp.equals(value);
    }

}
