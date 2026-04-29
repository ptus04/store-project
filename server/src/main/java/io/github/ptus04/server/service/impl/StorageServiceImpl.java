package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.config.StorageProperties;
import io.github.ptus04.server.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final StorageProperties storageProperties;

    @Override
    @SneakyThrows
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        
        Path root = Paths.get(storageProperties.getPath() != null ? storageProperties.getPath() : "uploads");
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        Files.copy(file.getInputStream(), root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }
}
