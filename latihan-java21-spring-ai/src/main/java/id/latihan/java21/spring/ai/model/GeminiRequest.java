package id.latihan.java21.spring.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public @Data class GeminiRequest {

    private List<Content> contents;

}
