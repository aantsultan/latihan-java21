package id.latihan.java21.spring.ai.config;

import id.latihan.java21.spring.ai.service.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class GeminiAiConfig {

    private final ApplicationProperties properties;

    @Bean
    public WebClient geminiWebClient(){
        return WebClient.builder()
                .baseUrl(properties.getGeminiApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
