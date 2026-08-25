package com.rikkeipay.config;

import io.langfuse.client.LangfuseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangfuseConfig {

    @Value("${langfuse.public-key}")
    private String publicKey;

    @Value("${langfuse.secret-key}")
    private String secretKey;

    @Value("${langfuse.base-url}")
    private String baseUrl;

    @Bean
    public LangfuseClient langfuseClient() {
        // API Keys được lấy từ application.yml thông qua biến môi trường, an toàn bảo mật.
        return new LangfuseClient(publicKey, secretKey, baseUrl);
    }
}
