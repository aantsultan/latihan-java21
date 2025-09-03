package id.latihan.java21.spring.ai.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Health", description = "The Health API is used to make sure the service is running up.")
public class HealthController {

    @GetMapping(value = {"/health", "/health/"})
    public String health(){
        return "Chatbot API is up and running";
    }

}
