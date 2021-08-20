package com.dongppman.community.config;


import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data//必须实现setter方法才可以
@ConfigurationProperties(prefix = "elasticsearch")
@Configuration
public class ElasticsearchConfig {
    private String host;
    private Integer port;
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        //在这里配置你的elasticsearch的情况
                        new HttpHost(host, port)
                )
        );
        return client;
    }
}
