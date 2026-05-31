package io.github.ptus04.server.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static io.github.ptus04.server.redis.config.RedisStreamListenerConfig.CONSUMER_GROUP;
import static io.github.ptus04.server.redis.config.RedisStreamListenerConfig.CONSUMER_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamRetryService {
    private final StringRedisTemplate stringRedisTemplate;
    private final Map<String, StreamListener<String, MapRecord<String, String, String>>> listenerMap;

    @Scheduled(fixedDelay = 10000)
    public void retryGlobal() {
        listenerMap.forEach((beanName, listener) -> {
            String suffix = beanName.replace("StreamListener", "").toLowerCase();
            String streamKey = "stream:" + suffix;

            PendingMessages pendingMessages = stringRedisTemplate.opsForStream().pending(streamKey, CONSUMER_GROUP, Range.unbounded(), 100);

            for (PendingMessage pendingMessage : pendingMessages) {
                if (pendingMessage.getElapsedTimeSinceLastDelivery().toMinutes() >= 1) {
                    List<MapRecord<String, String, String>> claimed = stringRedisTemplate.<String, String>opsForStream().claim(
                            streamKey,
                            CONSUMER_GROUP,
                            CONSUMER_NAME,
                            RedisStreamCommands.XClaimOptions.minIdle(Duration.ofMinutes(1)).ids(pendingMessage.getId())
                    );

                    for (MapRecord<String, String, String> record : claimed) {
                        listener.onMessage(record);
                        stringRedisTemplate.opsForStream().acknowledge(CONSUMER_GROUP, record);
                    }
                }
            }
        });
    }
}
