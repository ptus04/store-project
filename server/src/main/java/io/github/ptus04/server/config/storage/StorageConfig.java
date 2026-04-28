package io.github.ptus04.server.config.storage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobCorsRule;
import com.azure.storage.blob.models.BlobServiceProperties;
import com.azure.storage.blob.models.PublicAccessType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@RequiredArgsConstructor
public class StorageConfig {

    @Bean
    public CommandLineRunner setupAzurite(BlobServiceClient blobServiceClient) {
        return args -> {
            try {
                // Configure CORS
                BlobServiceProperties properties = blobServiceClient.getProperties();
                BlobCorsRule corsRule = new BlobCorsRule()
                        .setAllowedOrigins("*")
                        .setAllowedMethods("GET,POST,PUT,DELETE")
                        .setAllowedHeaders("*")
                        .setExposedHeaders("*")
                        .setMaxAgeInSeconds(3600);
                properties.setCors(Collections.singletonList(corsRule));
                blobServiceClient.setProperties(properties);

                // Ensure 'avatars' container is public
                var avatarContainer = blobServiceClient.getBlobContainerClient("avatars");
                if (!avatarContainer.exists()) {
                    avatarContainer.createWithResponse(null, PublicAccessType.BLOB, null, null);
                } else {
                    avatarContainer.setAccessPolicy(PublicAccessType.BLOB, null);
                }

                // Ensure 'products' container is public
                var productContainer = blobServiceClient.getBlobContainerClient("products");
                if (!productContainer.exists()) {
                    productContainer.createWithResponse(null, PublicAccessType.BLOB, null, null);
                } else {
                    productContainer.setAccessPolicy(PublicAccessType.BLOB, null);
                }

                // Ensure 'images' container is public
                var imageContainer = blobServiceClient.getBlobContainerClient("images");
                if (!imageContainer.exists()) {
                    imageContainer.createWithResponse(null, PublicAccessType.BLOB, null, null);
                } else {
                    imageContainer.setAccessPolicy(PublicAccessType.BLOB, null);
                }

                System.out.println("Azure Storage (Azurite) initialized with CORS and Public Access.");
            } catch (Exception e) {
                System.err.println("Warning: Could not initialize Azurite properties: " + e.getMessage());
            }
        };
    }

}
