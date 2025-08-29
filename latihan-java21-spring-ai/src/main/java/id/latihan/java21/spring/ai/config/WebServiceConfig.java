package id.latihan.java21.spring.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebServiceConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
