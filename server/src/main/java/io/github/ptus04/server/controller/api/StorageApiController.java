package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.StorageSasResponse;
import io.github.ptus04.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/blobs")
public class StorageApiController {
    private final StorageService storageService;

    @GetMapping(path = "/{containerName}/sas")
    public ResponseEntity<StorageSasResponse> getBlobUploadUrl(@PathVariable String containerName) {
        return ResponseEntity.ok().body(storageService.createBlobUploadUrl(containerName));
    }

    @GetMapping(path = "/{containerName}/sas", params = "blobName")
    public ResponseEntity<StorageSasResponse> createBlobUploadUrl(@PathVariable String containerName,
                                                                  @RequestParam String blobName) {
        return ResponseEntity.ok().body(storageService.createBlobUploadUrl(containerName, blobName));
    }

    @DeleteMapping(path = "/{containerName}/{blobName}")
    public ResponseEntity<Void> deleteBlob(@PathVariable String containerName, @PathVariable String blobName) {
        boolean result = storageService.deleteBlob(containerName, blobName);
        return result ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

