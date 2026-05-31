package io.github.ptus04.server.redis.config;

import io.lettuce.core.RedisCommandExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedisStreamListenerConfig {
    public static final String CONSUMER_NAME = "store-server-node-1";
    public static final String CONSUMER_GROUP = "store-server";

    public static final String ORDER_STREAM_KEY = "stream:order";
    public static final String INVOICE_STREAM_KEY = "stream:invoice";

    private final RedisConnectionFactory connectionFactory;
    private final StringRedisTemplate stringRedisTemplate;
    private final Map<String, StreamListener<String, MapRecord<String, String, String>>> listenerMap;

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer() {
        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(3))
                .build();
        var container = StreamMessageListenerContainer.create(connectionFactory, options);
        var consumer = Consumer.from(CONSUMER_GROUP, CONSUMER_NAME);

        listenerMap.forEach((beanName, listener) -> {
            String suffix = beanName.replace("StreamListener", "").toLowerCase();
            String streamKey = "stream:" + suffix;
            createGroupIfNotExists(streamKey);
            container.receive(consumer, StreamOffset.create(streamKey, ReadOffset.lastConsumed()), listener);
        });

        container.start();
        return container;
    }

    private void createGroupIfNotExists(String streamKey) {
        try {
            stringRedisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), CONSUMER_GROUP);
        } catch (RedisSystemException e) {
            if (e.getRootCause() instanceof RedisCommandExecutionException redisEx) {
                if (redisEx.getMessage().contains("BUSYGROUP")) {
                    return;
                }
            }
            throw e;
        }
    }
}
