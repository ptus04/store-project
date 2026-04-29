package io.github.ptus04.server.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file);
}
