package id.latihan.java21.spring.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.latihan.java21.spring.ai.exception.ApplicationException;
import id.latihan.java21.spring.ai.service.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiServiceImpl implements GeminiAiService {

    private final ApplicationProperties properties;
    private final WebClient geminiWebClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_CATEGORY = "category";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_CANDIDATES = "candidates";
    private static final String KEY_PARTS = "parts";
    private static final String KEY_CONTENTS = "contents";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TEXT = "text";

    private static final String SC_CATEGORY = "CATEGORY:";
    private static final String SC_RESPONSE = "RESPONSE:";

    @Override
    public String generateResponse(String query) {
        try {
            String fullPrompt = """
                    You are a helpful customer support chatbot. Provide a concise and friendly response to this customer query : %s
                    """
                    .formatted(query);
            String geminiApiModel = properties.getGeminiApiModel();
            String geminiApiKey = properties.getGeminiApiKey();

            Map<String, Object> partObject = new HashMap<>();
            partObject.put("text", fullPrompt);

            Map<String, Object> contentObject = new HashMap<>();
            contentObject.put(KEY_PARTS, List.of(partObject));
            contentObject.put(KEY_ROLE, "user");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(KEY_CONTENTS, List.of(contentObject));

            log.debug("Sending request to Gemini API :");
            log.debug("URL: {}?key={}...", geminiApiModel, geminiApiKey.substring(0, 5));
            log.debug("Request body : {}", requestBody);

            // Make the API Request with more detailed error handling
            Map<String, Object> response = geminiWebClient.post()
                    .uri(String.format("%s?key={apikey}", geminiApiModel), geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Response received : {}", response);

            // Extract the text from the response
            if (response != null && response.containsKey(KEY_CANDIDATES)) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get(KEY_CONTENTS);
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get(KEY_CONTENT);
                    List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get(KEY_PARTS);
                    if (!responseParts.isEmpty()) {
                        return (String) responseParts.get(0).get(KEY_TEXT);
                    }
                }
            }

            return "I'm sorry, I couldn't process your request.";
        } catch (WebClientResponseException e) {
            int statusCode = e.getStatusCode().value();
            HttpStatusCode httpStatusCode = e.getStatusCode();
            String statusText = e.getStatusText();
            log.error("Error calling Gemini API : {} {}", e.getStatusCode(), e.getStatusText());
            log.error("Response body : {}", e.getResponseBodyAsString());

            return switch (statusCode) {
                case 400 ->
                        "I'm sorry, there was an error with the request format (400 Bad Request). Please ensure your API key is correct and try again.";
                case 401, 403 -> "I'm sorry, there was an authentication error. Please check your API key.";
                case 429 -> "I'm sorry, we've hit the rate limit for the Gemini API. Please try again in a minute.";
                default ->
                        String.format("I'm sorry, there was an error calling the Gemini API: %s %s", httpStatusCode, statusText);
            };
        } catch (Exception e) {
            String message = e.getMessage();
            log.error("Unexpected error calling Gemini API: {}", message, e);
            return String.format("I'm sorry, there was an unexpected error processing your request: %s", message);
        }
    }

    @Override
    public Map<String, String> generateResponseV2(String query) {
        try {
            String geminiApiModel = properties.getGeminiApiModel();
            String geminiApiKey = properties.getGeminiApiKey();

            // Create a prompt that asks for both an answer and a category
            String fullPrompt = """
                    You are a helpful customer support chatbot.
                    Provide a response to this customer query: '%s'.
                    Also classify this query into exactly ONE of these categories:
                    'account', 'billing', 'technical', or 'general'.
                    Format your response as follows:
                    CATEGORY: [the category]
                    RESPONSE: [your helpful response]
                    """
                    .formatted(query);

            // Create the request body with proper structure (same as before)
            Map<String, Object> partObject = new HashMap<>();
            partObject.put(KEY_TEXT, fullPrompt);

            Map<String, Object> contentObject = new HashMap<>();
            contentObject.put(KEY_PARTS, List.of(partObject));
            contentObject.put(KEY_ROLE, "user");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(KEY_CONTENTS, List.of(contentObject));

            // Make the API request (same as before)
            Map<String, Object> response = geminiWebClient.post()
                    .uri(String.format("%s?key={apiKey}", geminiApiModel), geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Extract the text from the response
            String responseText = "";
            if (response != null && response.containsKey(KEY_CANDIDATES)) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get(KEY_CANDIDATES);
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get(KEY_CONTENT);
                    List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get(KEY_PARTS);
                    if (!responseParts.isEmpty()) {
                        responseText = (String) responseParts.get(0).get(KEY_TEXT);
                    }
                }
            }

            // Parse the category and response from the text
            Map<String, String> result = new HashMap<>();

            if (responseText.contains(SC_CATEGORY) && responseText.contains(SC_RESPONSE)) {
                String category = responseText
                        .substring(responseText.indexOf(SC_CATEGORY) + 9, responseText.indexOf(SC_RESPONSE))
                        .trim();

                String chatResponse = responseText
                        .substring(responseText.indexOf(SC_RESPONSE) + 9)
                        .trim();

                result.put(KEY_CATEGORY, category);
                result.put(KEY_RESPONSE, chatResponse);
            } else {
                // Fallback if the format is not as expected
                result.put(KEY_CATEGORY, "general");
                result.put(KEY_RESPONSE, responseText);
            }

            return result;

        } catch (Exception e) {
            // Handle errors (same as before)
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put(KEY_CATEGORY, "error");
            errorResult.put(KEY_RESPONSE, String.format("I'm sorry, there was an error processing your request: %s", e.getMessage()));
            return errorResult;
        }
    }

    /***
     * Contoh dari https://aistudio.google.com 2025-09-01 07:06 WIB
     * curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent" \
     *   -H 'Content-Type: application/json' \
     *   -H 'X-goog-api-key: GEMINI_API_KEY' \
     *   -X POST \
     *   -d '{
     *     "contents": [
     *       {
     *         "parts": [
     *           {
     *             "text": "Explain how AI works in a few words"
     *           }
     *         ]
     *       }
     *     ]
     *   }'
     * @param issueType
     * @return
     */
    @Override
    public String generateTemplate(String issueType) {
        try {
            // Build the request body using nested Maps
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", """
                    Create a professional customer support response template for the following issue type: '%s'.
                    Include placeholders like [CUSTOMER_NAME], [TICKET_NUMBER], [SPECIFIC_DETAILS] where appropriate.
                    Make it friendly, professional, and helpful. Keep it concise but comprehensive.
                    """.formatted(issueType));



            Map<String, Object> part = new HashMap<>();
            part.put(KEY_PARTS, List.of(textPart));
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(KEY_CONTENTS, List.of(part));
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-goog-api-key", properties.getGeminiApiKey());
            // Create the request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            // Make the API call
            String urlWithKey = String.format("%s%s", properties.getGeminiApiUrl(), properties.getGeminiApiModel());
            String response = restTemplate.postForObject(urlWithKey, requestEntity, String.class);
            // Parse the response
            JsonNode jsonResponse = objectMapper.readTree(response);
            // Extract the generated text from the response
            return jsonResponse
                    .path(KEY_CANDIDATES)
                    .get(0)
                    .path(KEY_CONTENT)
                    .path(KEY_PARTS)
                    .get(0)
                    .path(KEY_TEXT)
                    .asText();
        } catch (JsonProcessingException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }
}
