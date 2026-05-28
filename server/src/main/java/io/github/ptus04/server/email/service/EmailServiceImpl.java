package io.github.ptus04.server.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

@Slf4j
@RequiredArgsConstructor
@Profile("prod")
@Primary
@Service
public class EmailServiceImpl implements EmailService {
    private static final String EMAIL_OTP_KEY_PREFIX = "email:otp:";
    private static final long EMAIL_OTP_TTL_SECONDS = 300;

    private final RandomGenerator randomGenerator = new SecureRandom();
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendInvoiceEmail(String toEmail, String orderCode, String invoiceLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Thanh toán đơn hàng #" + orderCode + " thành công");
            helper.setText(buildInvoiceHtmlString(orderCode, invoiceLink), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.atError()
                    .setMessage("Failed to send invoice email to {} for order {}")
                    .addArgument(toEmail)
                    .addArgument(orderCode)
                    .setCause(e)
                    .log();
        }
    }

    @Override
    public long sendEmailVerificationOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        String key = EMAIL_OTP_KEY_PREFIX + normalizedEmail;
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            String otp = String.format("%06d", randomGenerator.nextInt(0, 1_000_000));
            redisTemplate.opsForValue().set(key, otp, EMAIL_OTP_TTL_SECONDS, TimeUnit.SECONDS);
            sendEmailVerificationOtpMessage(normalizedEmail, otp);
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

    private void sendEmailVerificationOtpMessage(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Mã OTP xác thực email SLY");
            helper.setText(buildEmailOtpHtml(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.warn("Could not send email verification OTP to {}", email, e);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String buildInvoiceHtmlString(String orderCode, String invoiceLink) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"/>
                <style>
                  body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 20px; }
                  .card { max-width: 560px; margin: auto; background: #fff;
                          border-radius: 8px; overflow: hidden;
                          box-shadow: 0 2px 8px rgba(0,0,0,.1); }
                  .header { background: #4CAF50; color: #fff; padding: 24px; text-align: center; font-size: 20px; }
                  .body   { padding: 28px; color: #333; font-size: 15px; line-height: 1.8; }
                  .code   { font-size: 22px; font-weight: bold; color: #4CAF50; letter-spacing: 2px; }
                  .btn    { display: inline-block; margin-top: 20px; padding: 12px 28px;
                            background: #4CAF50; color: #fff; text-decoration: none;
                            border-radius: 6px; font-weight: bold; }
                  .link   { font-size: 12px; color: #888; margin-top: 10px; word-break: break-all; }
                  .footer { background: #f0f0f0; text-align: center; padding: 14px;
                            font-size: 12px; color: #999; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">🎉 Đặt hàng thành công!</div>
                  <div class="body">
                    <p>Cảm ơn bạn đã mua hàng!</p>
                    <p>Mã đơn hàng của bạn:</p>
                    <p class="code">#%s</p>
                    <p>Nhấn bên dưới để xem chi tiết hóa đơn:</p>
                    <a href="%s" class="btn" style="color:#fff !important;">📄 Xem Hóa Đơn</a>
                  </div>
                </div>
                </body>
                </html>
                """.formatted(orderCode, invoiceLink);
    }

    private String buildEmailOtpHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head><meta charset="UTF-8"/>
                <style>
                  body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 20px; }
                  .card { max-width: 560px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.1); }
                  .header { background: #111827; color: #fff; padding: 24px; text-align: center; font-size: 20px; }
                  .body { padding: 28px; color: #333; font-size: 15px; line-height: 1.8; }
                  .code { font-size: 30px; font-weight: bold; color: #111827; letter-spacing: 6px; text-align: center; }
                  .footer { background: #f0f0f0; text-align: center; padding: 14px; font-size: 12px; color: #777; }
                </style>
                </head>
                <body>
                <div class="card">
                  <div class="header">Xác thực email SLY</div>
                  <div class="body">
                    <p>Mã OTP xác thực email của bạn là:</p>
                    <p class="code">%s</p>
                    <p>Mã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho người khác.</p>
                  </div>
                  <div class="footer">SLY Store</div>
                </div>
                </body>
                </html>
                """.formatted(otp);
    }
}
