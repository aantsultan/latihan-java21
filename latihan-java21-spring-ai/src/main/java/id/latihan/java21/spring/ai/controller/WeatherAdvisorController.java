package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.WeatherAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherAdvisorController {

    private final WeatherAdvisorService weatherAdvisorService;

    @GetMapping("/clothing-advice")
    public String getClothingAdvice(@RequestParam String weather,
                                    @RequestParam int temperature) {
        return weatherAdvisorService.getClothingAdvice(weather, temperature);
    }

}
