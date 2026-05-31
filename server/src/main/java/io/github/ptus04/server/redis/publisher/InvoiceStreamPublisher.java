package io.github.ptus04.server.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ptus04.server.redis.config.RedisStreamListenerConfig;
import io.github.ptus04.server.event.InvoiceCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceStreamPublisher {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleOrderPaidEvent(InvoiceCreatedEvent invoiceCreatedEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(invoiceCreatedEvent);
        stringRedisTemplate.opsForStream().add(RedisStreamListenerConfig.INVOICE_STREAM_KEY, Map.of("payload", json));
    }
}
