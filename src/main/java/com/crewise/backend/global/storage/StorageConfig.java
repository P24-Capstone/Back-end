package com.crewise.backend.global.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public StorageService storageService(
            @Value("${storage.type:local}") String type,
            @Value("${storage.local.upload-dir:uploads}") String uploadDir,
            @Value("${storage.local.base-url:http://localhost:8080/uploads/}") String baseUrl) {

        if ("s3".equals(type)) {
            return new S3StorageService();
        }
        return new LocalStorageService(uploadDir, baseUrl);
    }
}
