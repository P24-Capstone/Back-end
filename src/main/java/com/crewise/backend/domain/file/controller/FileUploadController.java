package com.crewise.backend.domain.file.controller;

import com.crewise.backend.global.common.ApiResponse;
import com.crewise.backend.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @AuthenticationPrincipal String userId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ApiResponse.fail("파일이 비어 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ApiResponse.fail("이미지 파일만 업로드 가능합니다.");
        }

        String url = storageService.upload(file);
        return ApiResponse.ok(url);
    }
}
