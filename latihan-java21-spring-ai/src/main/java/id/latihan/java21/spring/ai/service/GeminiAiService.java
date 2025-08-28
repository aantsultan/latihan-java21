package id.latihan.java21.spring.ai.service;

import java.util.Map;

public interface GeminiAiService {

    String generateResponse(String query);
    Map<String, String> generateResponseV2(String query);

}
