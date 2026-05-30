package io.github.ptus04.server.redis.listener;

import io.github.ptus04.server.redis.config.RedisStreamListenerConfig;
import io.github.ptus04.server.event.OrderPaidEvent;
import io.github.ptus04.server.service.InvoiceService;
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
public class OrderStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final InvoiceService invoiceService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String streamKey = message.getStream();
        String jsonPayload = message.getValue().get("payload");
        OrderPaidEvent orderPaidEvent = objectMapper.readValue(jsonPayload, OrderPaidEvent.class);
        invoiceService.createInvoice(orderPaidEvent);
        redisTemplate.opsForStream().acknowledge(streamKey, RedisStreamListenerConfig.CONSUMER_GROUP, message.getId());
    }
}
