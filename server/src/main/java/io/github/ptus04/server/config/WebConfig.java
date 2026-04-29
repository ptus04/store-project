package io.github.ptus04.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = storageProperties.getPath() != null ? storageProperties.getPath() : "uploads";
        String absolutePath = Paths.get(path).toAbsolutePath().toString().replace("\\", "/");
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + absolutePath);


    }
}
