package io.github.ptus04.server.redis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

import static io.github.ptus04.server.redis.config.RedisStreamListenerConfig.CONSUMER_GROUP;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamTrimService {
    private final StringRedisTemplate stringRedisTemplate;
    private final Map<String, StreamListener<String, MapRecord<String, String, String>>> listenerMap;

    @Scheduled(fixedRate = 300000)
    public void safelyTrimOrdersStream() {
        listenerMap.forEach((beanName, listener) -> {
            String suffix = beanName.replace("StreamListener", "").toLowerCase();
            String streamKey = "stream:" + suffix;

            PendingMessagesSummary summary = stringRedisTemplate.opsForStream().pending(streamKey, CONSUMER_GROUP);

            if (summary == null || summary.getTotalPendingMessages() == 0) {
                stringRedisTemplate.opsForStream().trim(streamKey, 100, true);
                return;
            }

            String oldestPendingId = summary.minMessageId();

            stringRedisTemplate.execute((RedisCallback<?>) connection -> {
                byte[][] args = new byte[][]{
                        streamKey.getBytes(),
                        "MINID".getBytes(),
                        "~".getBytes(),
                        oldestPendingId.getBytes()
                };

                return connection.execute("XTRIM", args);
            });
        });
    }
}
