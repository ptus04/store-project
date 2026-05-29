package io.github.ptus04.server.controller;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MediaFallbackController {

    private final BlobServiceClient blobServiceClient;

    @GetMapping("/local-media/{container}/{filename:.+}")
    public ResponseEntity<?> getMediaOrFallback(
            @PathVariable String container,
            @PathVariable String filename) {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(container);
            BlobClient blobClient = containerClient.getBlobClient(filename);

            if (blobClient.exists()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                blobClient.downloadStream(outputStream);
                byte[] bytes = outputStream.toByteArray();

                String contentType = "image/jpeg";
                if (filename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.endsWith(".webp")) {
                    contentType = "image/webp";
                } else if (filename.endsWith(".gif")) {
                    contentType = "image/gif";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(bytes);
            }
        } catch (Exception e) {
            log.warn("Failed to load image from Azure Storage emulator: container={}, filename={}. Using fallback redirect.", container, filename, e);
        }

        // Fallback redirection to premium Unsplash mockup images
        String fallbackUrl = getFallbackImageUrl(filename);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, fallbackUrl)
                .build();
    }

    private String getFallbackImageUrl(String filename) {
        String name = filename.toLowerCase();

        // 1. Specific Banners
        if (name.equals("banner.jpg")) {
            // Premium landscape leather wallet banner
            return "https://images.unsplash.com/photo-1627124118303-624c8949ee56?q=80&w=1200";
        }
        if (name.equals("banner_mobile.webp")) {
            // Elegant mobile portrait leather wallet
            return "https://images.unsplash.com/photo-1627124118303-624c8949ee56?q=80&w=600";
        }
        if (name.equals("cartoon_web.webp")) {
            // Landscape graphic tee fashion model
            return "https://images.unsplash.com/photo-1576566588028-4147f3842f27?q=80&w=1200";
        }

        // 2. Wallets
        if (name.contains("wallet")) {
            return "https://images.unsplash.com/photo-1590534247854-e97d5e3fe36c?q=80&w=600";
        }

        // 3. Clothing / Shirts
        if (name.contains("tee") || name.contains("shirt") || name.contains("polo")) {
            return "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=600";
        }
        if (name.contains("jacket") || name.contains("hoodie") || name.contains("ziptan") || name.contains("antimon")) {
            return "https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=600";
        }

        // 4. Pants / Shorts
        if (name.contains("pants") || name.contains("jorts") || name.contains("short") || name.contains("knee")) {
            return "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?q=80&w=600";
        }

        // 5. Bags / Backpacks
        if (name.contains("backpack") || name.contains("bag")) {
            return "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?q=80&w=600";
        }

        // 6. Headwear
        if (name.contains("cap") || name.contains("hat")) {
            return "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?q=80&w=600";
        }

        // Generic beautiful clothing rack / fashion placeholder
        return "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?q=80&w=600";
    }
}
