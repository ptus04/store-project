package io.github.ptus04.server.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ptus04.server.redis.config.RedisStreamListenerConfig;
import io.github.ptus04.server.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStreamPublisher {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPaidEvent(OrderPaidEvent orderPaidEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderPaidEvent);
        stringRedisTemplate.opsForStream().add(RedisStreamListenerConfig.ORDER_STREAM_KEY, Map.of("payload", json));
    }
}
