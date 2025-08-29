package id.latihan.java21.spring.ai.service;

import id.latihan.java21.spring.ai.service.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketClassificationServiceImpl implements TicketClassificationService {

    private final ApplicationProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public String classifyTicket(String ticketContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getOpenaiApiKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("input", new Object[]{
                Map.of("role", "system",
                        "content", """
                                You are a ticket classification system. Classify the following support ticket into one of these categories :
                                'Technical Issue',
                                'Billing Question',
                                'Feature Request', or
                                'General Inquiry'
                                """),
                Map.of("role", "user",
                        "content", ticketContent)
        });

        requestBody.put("store", true);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(properties.getOpenaiApiUrl(), requestEntity, Map.class);

        if (response != null && response.containsKey("choises")) {

            Object[] choises = (Object[]) response.get("choises");
            Map<String, Object> firstChoice = (Map<String, Object>) choises[0];
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

            String content = (String) message.get("content");
            return content.trim();
        }
        return "Uncategorized";
    }
}
