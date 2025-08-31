package id.latihan.java21.spring.ai.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class OllamaWeatherAdviceTest {

    private static final int PORT = 11434;

    @Container
    static GenericContainer<?> ollamaContainer = new GenericContainer<>("ollama/ollama:test")
            .withExposedPorts(PORT);

    @DynamicPropertySource
    static void ollamaProperties(DynamicPropertyRegistry registry){
        registry.add("spring.ai.ollama.base-url",()
                -> String.format("http://%s:%d", ollamaContainer.getHost(), ollamaContainer.getMappedPort(PORT)));
    }

    @Test
    void testWithOllama(){
        String baseUrl = String.format("http://%s:%d", ollamaContainer.getHost(), ollamaContainer.getMappedPort(PORT));
//        OllamaApi ollamaApi = new OllamaApi(baseUrl);
        //OllamaChatModel ollamaChatModel = new OllamaChatModel(ollamaApi);

        // create weather advisor Ollama
        //WeatherAdvisorService service = new WeatherAdvisorServiceImpl(ollamaChatModel);
        //String advice = service.getClothingAdvice("snowy", -5);
        String advice = "";

        Assertions.assertNotNull(advice);
        Assertions.assertTrue(advice.contains("coat") || advice.contains("jacket") || advice.contains("warm"));
    }
}
