package id.latihan.java21.spring.ai.helper;

import lombok.Getter;

@Getter
public enum SentimentDistribution {
    VERY_NEGATIVE("Very Negative"),
    NEGATIVE("Negative"),
    NEUTRAL("Neutral"),
    POSITIVE("Positive"),
    VERY_POSITIVE("Very Positive");

    final String name;

    SentimentDistribution(String name) {
        this.name = name;
    }
}
