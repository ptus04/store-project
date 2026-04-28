package io.github.ptus04.server;

import com.azure.spring.cloud.autoconfigure.implementation.storage.queue.AzureStorageQueueAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = AzureStorageQueueAutoConfiguration.class)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }


}
