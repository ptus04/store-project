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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AzureStorageServiceImpl implements StorageService {
    private static final Duration SAS_EXPIRATION = Duration.ofMinutes(15);

    private final BlobServiceClient serviceClient;

    @Override
    public StorageSasResponse createBlobUploadUrl(String containerName) {
        return createBlobUploadUrl(containerName, UUID.randomUUID().toString());
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
        return new StorageSasResponse(url, expiration);
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
