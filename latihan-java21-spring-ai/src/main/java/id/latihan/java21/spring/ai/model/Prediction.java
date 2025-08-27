package id.latihan.java21.spring.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class Prediction {

    private String label;
    private double probability;

}
