package id.latihan.java21.spring.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class ChatResponseV2 {

    private String response;
    private String category;

}
