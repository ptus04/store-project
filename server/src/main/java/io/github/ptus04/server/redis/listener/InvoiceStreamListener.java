package io.github.ptus04.server.redis.listener;

import io.github.ptus04.server.redis.config.RedisStreamListenerConfig;
import io.github.ptus04.server.event.InvoiceCreatedEvent;
import io.github.ptus04.server.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String streamKey = message.getStream();
        String jsonPayload = message.getValue().get("payload");
        InvoiceCreatedEvent invoiceCreatedEvent = objectMapper.readValue(jsonPayload, InvoiceCreatedEvent.class);
        emailService.sendInvoiceEmail(invoiceCreatedEvent.email(), invoiceCreatedEvent.orderCode(), invoiceCreatedEvent.invoiceLink());
        redisTemplate.opsForStream().acknowledge(streamKey, RedisStreamListenerConfig.CONSUMER_GROUP, message.getId());
    }
}
