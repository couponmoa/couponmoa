package com.couponmoa.backend.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris:http://elasticsearch:9200}")
    private String elasticsearchUri;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = HttpHost.create(elasticsearchUri);
        return new RestHighLevelClient(
                RestClient.builder(httpHost)
        );
    }
}