package com.caffeaulait.dianping.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchRestClient {

    @Value("${elasticsearch.ip}")
    private String address;

    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient() {
        String[] strings = address.split(":");
        String ip = strings[0];
        int port = Integer.parseInt(strings[1]);
        HttpHost httpHost = new HttpHost(ip, port, "http");
        return new RestHighLevelClient(RestClient.builder(httpHost));
    }
}
