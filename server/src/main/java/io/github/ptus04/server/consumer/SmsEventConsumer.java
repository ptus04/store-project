package io.github.ptus04.server.consumer;

import com.twilio.rest.verify.v2.service.Verification;
import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.config.TwilioConfig;
import io.github.ptus04.server.config.TwilioProperties;
import io.github.ptus04.server.event.OtpRequestedEvent;
import io.github.ptus04.server.service.SMSVerificationService;
import io.github.ptus04.server.util.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@ConditionalOnBean(TwilioConfig.class)
@Component
@RequiredArgsConstructor
public class SmsEventConsumer {
    private final TwilioProperties twilioProperties;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SEND_OTP)
    public void handleSendOtpEvent(OtpRequestedEvent otpRequestedEvent) {
        Verification.creator(
                twilioProperties.getVerify().getServiceSid(),
                PhoneNumberUtils.prefixWithVietnameseCode(otpRequestedEvent.phone()),
                Verification.Channel.SMS.toString()
        ).create();
    }
}
