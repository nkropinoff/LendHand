package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.exception.FileNotUploadException;
import com.lendhand.app.lendhandservice.exception.MinioBucketNotInitialized;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageService {

    private final MinioClient minioClient;

    @Autowired
    public FileStorageService (MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.bucket.name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new MinioBucketNotInitialized("Cound not initialize bucket for file storage", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File if empty or filename is null");
        }

        try (InputStream is = file.getInputStream()) {
            String objectName = generateObjectName(file.getOriginalFilename());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            return objectName;
        } catch (Exception e) {
            throw new FileNotUploadException("File cound not upload to file storage", e);
        }
    }

    private String generateObjectName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("\\s", "_");
    }




}
