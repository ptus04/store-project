package io.github.ptus04.server.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocalEmailServiceImpl implements EmailService {
    private static final String EMAIL_OTP_KEY_PREFIX = "email:otp:";
    private static final long EMAIL_OTP_TTL_SECONDS = 300;

    private final RandomGenerator randomGenerator = new SecureRandom();
    private final StringRedisTemplate redisTemplate;

    @Override
    public void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink) {
        log.atInfo()
                .setMessage("Simulating sending email to {} for order {} with invoice link: {}")
                .addArgument(toEmail)
                .addArgument(orderCode)
                .addArgument(invoiceLink)
                .log();
    }

    @Override
    public long sendEmailVerificationOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        String key = EMAIL_OTP_KEY_PREFIX + normalizedEmail;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            String otp = String.format("%06d", randomGenerator.nextInt(0, 1_000_000));
            redisTemplate.opsForValue().set(key, otp, EMAIL_OTP_TTL_SECONDS, TimeUnit.SECONDS);
            log.atInfo()
                    .setMessage("Generated OTP {} for email {} (normalized: {})")
                    .addArgument(otp)
                    .addArgument(email)
                    .addArgument(normalizedEmail)
                    .log();
        }

        Long expire = redisTemplate.getExpire(key);
        return expire == null || expire < 0 ? EMAIL_OTP_TTL_SECONDS : expire;
    }

    @Override
    public boolean verifyEmailOtp(String email, String otp) {
        String key = EMAIL_OTP_KEY_PREFIX + normalizeEmail(email);
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return false;
        }

        boolean verified = value.equals(otp);
        if (verified) {
            redisTemplate.delete(key);
        }
        return verified;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

}
