package com.dream.six.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.dream.six.utils.CommonDateUtils;
import com.dream.six.vo.response.S3UploadResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class StorageService {

    private final AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.fileBaseUrl}")
    private String fileBaseUrl;

    @Autowired
    public StorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public S3UploadResponseVO uploadFile(MultipartFile file, String fileType) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        String key = String.format("%s/%s/%s/%s/%s/%s/%s/%s", fileType, CommonDateUtils.getCurrentYear(), CommonDateUtils.getCurrentMonth(), CommonDateUtils.getCurrentDate(),  CommonDateUtils.getCurrentHour(),
                CommonDateUtils.getCurrentMinute(),
                CommonDateUtils.getCurrentSecond(), file.getOriginalFilename());
        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
        String s3Url = amazonS3.getUrl(bucketName, key).toString();
        log.info("File uploaded successfully to S3 at URL: {}", s3Url);
        return new S3UploadResponseVO(key, s3Url); // Return both key and URL
    }
    public byte[] downloadFileFromS3(String fileName) throws IOException {
        String desiredPart = fileName.substring(fileBaseUrl.length());
        S3Object s3Object =  amazonS3.getObject(bucketName,desiredPart);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }
    public byte[] downloadFileFromLocal(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found at the specified path: " + filePath);
        }
        return Files.readAllBytes(path);
    }
    public byte[] downloadFile(String fileName) throws IOException {
        String desiredPart = fileName.substring(fileBaseUrl.length());
        S3Object s3Object =  amazonS3.getObject(bucketName,desiredPart);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }
    public InputStream getFileAsStream(String filePath)  {
        String desiredPart = filePath.substring(fileBaseUrl.length());
        S3Object s3Object =  amazonS3.getObject(bucketName,desiredPart);
        return s3Object.getObjectContent();
    }
}
