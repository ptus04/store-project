package io.github.ptus04.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

@Configuration
public class RandomGeneratorConfig {
    @Bean
    public RandomGenerator randomGenerator() {
        return new SecureRandom();
    }
}
