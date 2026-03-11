package com.restaurant.storageservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {
    private final StorageProperties _properties;
    public R2Config(
            StorageProperties _properties
    ) {
        this._properties = _properties;
    }

    @Bean("r2")
    public S3Client s3Client(){
        StorageProperties.Cloudflare.R2 r2 = _properties.getCloudflare().getR2();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(r2.getAccessKey(), r2.getSecretKey());

        String r2Endpoint = String.format(r2.getUrl(), r2.getAccountId());

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                // R2 không dùng Region như AWS, nhưng SDK Java bắt buộc phải có, nên ta set đại là "auto" hoặc "us-east-1"
                .region(Region.of("auto"))
                .endpointOverride(URI.create(r2Endpoint))
                .build();
    }
}
