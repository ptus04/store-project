package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.StorageSasResponse;

public interface StorageService {

    StorageSasResponse createBlobUploadUrl(String containerName);

    StorageSasResponse createBlobUploadUrl(String containerName, String blobName);

    boolean deleteBlob(String containerName, String blobName);

}
