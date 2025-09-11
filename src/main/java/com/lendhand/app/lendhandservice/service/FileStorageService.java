package com.lendhand.app.lendhandservice.service;

import com.lendhand.app.lendhandservice.exception.FileNotDeletedException;
import com.lendhand.app.lendhandservice.exception.FileNotUploadException;
import com.lendhand.app.lendhandservice.exception.MinioBucketNotInitialized;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
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

    @Value("${minio.url}")
    private String minioUrl;

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

    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            String objectName = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (ErrorResponseException e ) {
            if (!e.errorResponse().code().equals("NoSuchKey")) {
                throw new FileNotDeletedException("File not deleted from MinIO", e);
            }
        } catch (Exception e ) {
            throw new FileNotDeletedException("File not deleted from MinIO", e);
        }
    }


    private String generateObjectName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("\\s", "_");
    }

    public String buildFileUrl(String objectName) {
        return minioUrl.replace("minio", "localhost") + "/" + bucketName + "/" + objectName;
    }

}
