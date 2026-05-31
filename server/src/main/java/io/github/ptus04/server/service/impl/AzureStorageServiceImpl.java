package io.github.ptus04.server.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import io.github.ptus04.server.dto.response.StorageSasResponse;
import io.github.ptus04.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AzureStorageServiceImpl implements StorageService {
    private static final Duration SAS_EXPIRATION = Duration.ofMinutes(15);
    private static final SecureRandom random = new SecureRandom();
    private final BlobServiceClient serviceClient;

    private UUID generateV7() {
        long timestamp = System.currentTimeMillis();
        long msecs = timestamp & 0xFFFFFFFFFFFFL; // 48-bit timestamp

        // High 64 bits: timestamp (48b) + version (4b) + rand_a (12b)
        long mostSigBits = (msecs << 16) | (0x7L << 12) | (random.nextLong() & 0xFFFL);

        // Low 64 bits: variant (2b) + rand_b (62b)
        // 0x8... sets the variant to 2 (10xx)
        long leastSigBits = (0x8000000000000000L) | (random.nextLong() & 0x3FFFFFFFFFFFFFFFL);

        return new UUID(mostSigBits, leastSigBits);
    }

    @Override
    public StorageSasResponse createBlobUploadUrl(String containerName) {
        return createBlobUploadUrl(containerName, generateV7().toString());
    }

    @Override
    public StorageSasResponse createBlobUploadUrl(String containerName, String blobName) {
        OffsetDateTime expiration = OffsetDateTime.now().plus(SAS_EXPIRATION);
        BlobSasPermission writePermission = new BlobSasPermission().setWritePermission(true);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                expiration,
                writePermission
        );

        BlobClient blobClient = createContainerIfNotExist(containerName).getBlobClient(blobName);
        String url = blobClient.getBlobUrl() + "?" + blobClient.generateSas(values);
        return new StorageSasResponse(blobName, url, expiration, Instant.now());
    }

    @Override
    public boolean deleteBlob(String containerName, String blobName) {
        return serviceClient.getBlobContainerClient(containerName)
                .getBlobClient(blobName).deleteIfExists();
    }

    private BlobContainerClient createContainerIfNotExist(String name) {
        BlobContainerClient container = serviceClient.getBlobContainerClient(name);
        container.createIfNotExists();
        return container;
    }

}
