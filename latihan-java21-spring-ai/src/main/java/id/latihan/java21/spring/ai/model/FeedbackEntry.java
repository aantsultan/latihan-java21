package id.latihan.java21.spring.ai.model;

import lombok.Data;

public @Data class FeedbackEntry {

    private int id;
    private String customer;
    private String department;
    private String date;
    private String comment;
    private String sentiment;

}
