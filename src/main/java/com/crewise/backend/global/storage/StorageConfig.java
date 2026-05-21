package com.crewise.backend.global.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Paths;

@Configuration
public class StorageConfig implements WebMvcConfigurer {

    @Value("${storage.type:local}")
    private String type;

    @Value("${storage.local.upload-dir:uploads}")
    private String uploadDir;

    @Value("${storage.local.base-url:http://localhost:8080/uploads/}")
    private String baseUrl;

    @Value("${storage.s3.bucket:}")
    private String bucket;

    @Value("${storage.s3.region:ap-northeast-2}")
    private String region;

    @Bean
    public StorageService storageService() {
        if ("s3".equals(type)) {
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
            return new S3StorageService(s3Client, bucket, region);
        }
        return new LocalStorageService(uploadDir, baseUrl);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!"s3".equals(type)) {
            String absolutePath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
            if (!absolutePath.endsWith("/")) absolutePath += "/";
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(absolutePath);
        }
    }
}