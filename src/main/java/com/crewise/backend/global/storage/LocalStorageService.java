package com.crewise.backend.global.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class LocalStorageService implements StorageService {

    private final String uploadDir;
    private final String baseUrl;

    public LocalStorageService(String uploadDir, String baseUrl) {
        this.uploadDir = uploadDir;
        this.baseUrl = baseUrl;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            Path dirPath = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(dirPath);

            String extension = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + extension;

            file.transferTo(dirPath.resolve(filename).toFile());

            return baseUrl + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
