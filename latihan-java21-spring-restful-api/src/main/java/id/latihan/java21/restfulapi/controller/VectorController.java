package id.latihan.java21.restfulapi.controller;

import id.latihan.java21.restfulapi.dto.WebResponse;
import id.latihan.java21.restfulapi.service.VectorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Vector API", description = "The Vector API. Provide calculation by using vector.")
public class VectorController {

    private final VectorService service;

    @GetMapping(value = "/api/vector",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> get() {
        return WebResponse.<String>builder()
                .data(service.get())
                .build();
    }

    @GetMapping(value = "/api/vector/multiply-skalar",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> multiplySkalar() {
        return WebResponse.<String>builder()
                .data(service.multiplySkalar())
                .build();
    }

    @GetMapping(value = "/api/vector/dot",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> dot() {
        return WebResponse.<String>builder()
                .data(service.dot())
                .build();
    }
}
