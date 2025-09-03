package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.model.ChatRequest;
import id.latihan.java21.spring.ai.model.ChatResponse;
import id.latihan.java21.spring.ai.model.ChatResponseV2;
import id.latihan.java21.spring.ai.service.GeminiAiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "The Chatbot API is used to create conversation between bot and user by using Gemini.")
public class ChatbotController {

    private final GeminiAiService geminiAiService;

    @PostMapping("/chatbot/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        if (request.getQuery() == null || request.getQuery().trim().isBlank()) {
            return new ChatResponse("Please provide a valid question or message.");
        }
        String aiResponse = geminiAiService.generateResponse(request.getQuery());
        return new ChatResponse(aiResponse);
    }

    @PostMapping("/chatbot/chat/v2")
    public ChatResponseV2 chatV2(@RequestBody ChatRequest request) {
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return new ChatResponseV2("Please provide a valid question or message.", "error");
        }

        Map<String, String> aiResponse = geminiAiService.generateResponseV2(request.getQuery());

        return new ChatResponseV2(aiResponse.get("response"), aiResponse.get("category"));
    }

}
