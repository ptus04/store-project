package io.github.ptus04.server.producer;

import io.github.ptus04.server.config.RabbitMQConfig;
import io.github.ptus04.server.event.OtpRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsEventProducer {
    private final AmqpTemplate amqpTemplate;

    public void publishOtpRequestedEvent(OtpRequestedEvent otpRequestedEvent) {
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_SMS, RabbitMQConfig.KEY_SMS_OTP_REQUESTED, otpRequestedEvent);
    }
}
