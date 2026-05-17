package com.crewise.backend.global.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * =====================================================================
 * AWS S3 파일 업로드 구현체 (현재 미사용 - 로컬 개발 시 LocalStorageService 사용)
 * =====================================================================
 *
 * [S3 전환 방법]
 *
 * Step 1. build.gradle에 AWS SDK 의존성 추가:
 *   implementation 'software.amazon.awssdk:s3:2.25.0'
 *
 * Step 2. application.yml에서 storage.type 변경:
 *   storage:
 *     type: s3           <-- 'local' 에서 's3' 로 변경
 *     s3:
 *       bucket: your-bucket-name
 *       region: ap-northeast-2
 *
 * Step 3. AWS 자격증명 설정 (방법 B 권장):
 *   방법 A - application.yml에 직접 입력 (보안상 비권장):
 *     storage.s3.access-key: AKIAXXXXXXXX
 *     storage.s3.secret-key: xxxxxxxxxxxxxxxx
 *
 *   방법 B - 환경변수로 설정 (권장):
 *     AWS_ACCESS_KEY_ID=AKIAXXXXXXXX
 *     AWS_SECRET_ACCESS_KEY=xxxxxxxxxxxxxxxx
 *     (IntelliJ: Run > Edit Configurations > Environment variables)
 *
 * Step 4. S3 버킷 퍼블릭 접근 허용 또는 CloudFront CDN 연결
 *
 * Step 5. 아래 구현 코드 주석 해제
 * =====================================================================
 */
public class S3StorageService implements StorageService {

    // @Value("${storage.s3.bucket}") private String bucket;
    // private final S3Client s3Client;

    @Override
    public String upload(MultipartFile file) {
        // String key = "teams/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
        //
        // s3Client.putObject(
        //     PutObjectRequest.builder()
        //         .bucket(bucket)
        //         .key(key)
        //         .contentType(file.getContentType())
        //         .build(),
        //     RequestBody.fromBytes(file.getBytes())
        // );
        //
        // return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;

        throw new UnsupportedOperationException("S3 의존성 추가 후 위 주석을 해제해 주세요.");
    }
}
