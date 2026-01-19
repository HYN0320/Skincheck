package com.skincheck_backend.infra.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {
        String ext = getExtension(file.getOriginalFilename());
        String fileName = "skin-analysis/" + UUID.randomUUID() + "." + ext;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return getFileUrl(fileName);
    }

    private String getFileUrl(String key) {
        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
