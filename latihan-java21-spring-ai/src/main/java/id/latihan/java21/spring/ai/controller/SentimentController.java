package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.SentimentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Sentiment", description = "The Sentiment API is used for predict the sentiments based on review/rating.")
public class SentimentController {

    private final SentimentService service;

    @GetMapping(value = "/sentiment",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadData() {
        return service.loadData();
    }

    @GetMapping(value = "/sentiment/v2",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadDataV2() {
        return service.loadDataV2();
    }

}
