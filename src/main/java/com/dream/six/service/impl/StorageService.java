package com.dream.six.service.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class StorageService {


    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.aKey}")
    private String aKey;

    @Value("${aws.Key}")
    private String Key;

    @Value("${aws.region}")
    private String region;

    private AmazonS3 awsS3Client;


    @Value("${aws.fileBaseUrl}")
    private String fileBaseUrl;

    @Bean
    public AmazonS3 getS3Client() {
        log.info("Initializing AWS S3 Client with Region: {}", region);

        ClientConfiguration clientConfiguration = new ClientConfiguration()
                .withConnectionTimeout(5000)
                .withSocketTimeout(15000)
                .withMaxConnections(300);

        awsS3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region) // Make sure this is correctly set
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(aKey, Key)))
                .withClientConfiguration(clientConfiguration)
                .build();

        return awsS3Client;
    }


    public String uploadFile(MultipartFile file, String type) throws IOException {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setContentDisposition("inline");

        String key = String.format("%s/%s",
                 type, file.getOriginalFilename());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
        awsS3Client.putObject(putObjectRequest);

        return awsS3Client.getUrl(bucketName, key).toString();
    }


    public byte[] downloadFile(String fileName) throws IOException {
        String desiredPart = fileName.substring(fileBaseUrl.length());
        S3Object s3Object = awsS3Client.getObject(bucketName, desiredPart);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    public InputStream getFileAsStream(String filePath)  {
        String desiredPart = filePath.substring(fileBaseUrl.length());
        S3Object s3Object = awsS3Client.getObject(bucketName, desiredPart);
        return s3Object.getObjectContent();
    }
    public void deleteFile(String longUrl) {
        String desiredPart = longUrl.substring(fileBaseUrl.length());
        awsS3Client.deleteObject(bucketName, desiredPart);

    }

}
