package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.RegressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegressionController {

    private final RegressionService service;

    @GetMapping(value = "/regression",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadData() {
        return service.loadData();
    }

    @GetMapping(value = "/regression/v2",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String loadDataV2() {
        return service.loadDataV2();
    }

}
