package id.latihan.java21.spring.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeatherAdvisorServiceImpl implements WeatherAdvisorService {

//    private final ChatClient chatClient;
//
//    public WeatherAdvisorServiceImpl(ChatClient.Builder chatClientBuilder) {
//        this.chatClient = chatClientBuilder.build();
//    }


    @Override
    public String getClothingAdvice(String weather, int temperature) {
//        String prompt = """
//                Given that the weather is %s and the temperature is %d degrees Celsius,
//                what clothes should I wear today ? Keep your answer brief and casual.
//                """.formatted(weather, temperature);

//        return chatClient.prompt(prompt).call().content();
        return "unused";
    }
}
