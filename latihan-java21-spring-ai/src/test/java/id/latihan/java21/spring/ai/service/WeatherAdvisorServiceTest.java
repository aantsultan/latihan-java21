package id.latihan.java21.spring.ai.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

@SpringBootTest
class WeatherAdvisorServiceTest {

    @MockitoBean
    private ChatClient chatClient;

    @Autowired
    private WeatherAdvisorService service;

    @Test
    void testGetClothingAdvice() {
        ChatClient.CallResponseSpec mockResponse = null;
        Mockito.when(chatClient.prompt(ArgumentMatchers.anyString()).call()).thenReturn(mockResponse);

        String advice = service.getClothingAdvice("sunny", 25);

        Assertions.assertNotNull(advice);
        Assertions.assertTrue(advice.length() > 0);
    }

    @Test
    void testAdviceRelevancy() {
        String weather = "rainy";
        int temperature = 10;
        String userText = """
                What should I wear when it's %s and %s degrees Celsius ?
                """.formatted(weather, temperature);

        String responseContent = service.getClothingAdvice(weather, temperature);

        ChatModel chatModel = null;
        RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        EvaluationRequest request = new EvaluationRequest(userText, Collections.emptyList(), responseContent);

        EvaluationResponse response = evaluator.evaluate(request);

        Assertions.assertTrue(response.isPass(), "Response should be relevant to this question");

    }

    @Test
    void testSimplePrompt(){
        String content = chatClient.prompt("Say 'Hello, World !'").call().content();
        Assertions.assertEquals("Hello, World !", content.trim());
    }

    @Test
    void testResponseContainsRequiredInfo(){
        String advice = service.getClothingAdvice("sunny", 30);
        Assertions.assertTrue(advice.toLowerCase().contains("hat") ||
                advice.toLowerCase().contains("sunscreen") ||
                advice.toLowerCase().contains("light"),
                "Hot weather advice should mention sun protection or light clothing");
    }

    @Test
    void testResponseLength(){
        String advice = service.getClothingAdvice("cloudy", 15);
        Assertions.assertTrue(advice.split("\\s+").length < 50,
                "Response should be concise (fewer than 50 words)");
    }

}
