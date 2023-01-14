package io.albot.lims.report.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SNSConfig {
    @Value("${sns_config.awsAccessKeyId}")
    private String awsAccessKeyId;
    @Value("${sns_config.secretKey}")
    private String secretKey;
    @Value("${sns_config.region}")
    private String region;

    @Bean
    public AmazonSNS getAmazonSNSObj() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, secretKey);
        return AmazonSNSClientBuilder.standard()
                .withRegion(Regions.fromName("us-east-1")).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }

    @Bean
    public AmazonS3 amazonS3Client() {

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, secretKey);

        return AmazonS3ClientBuilder
                .standard().withRegion(Regions.fromName(region)).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
