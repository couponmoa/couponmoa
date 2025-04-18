package com.couponmoa.backend.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.instance-profile:false}")
    private boolean useInstanceProfile;

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(region);

        if (useInstanceProfile) {
            // EC2에 부여된 IAM Role 기반 인증 (prod)
            builder = builder.withCredentials(new InstanceProfileCredentialsProvider(false));
        } else {
            // 로컬에서 access-key/secret-key 사용
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            builder = builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }

        return builder.build();
    }
}

