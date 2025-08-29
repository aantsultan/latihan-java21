package id.latihan.java21.spring.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class SupportTicket {

    private String id;
    private String content;
    private String category;

}
