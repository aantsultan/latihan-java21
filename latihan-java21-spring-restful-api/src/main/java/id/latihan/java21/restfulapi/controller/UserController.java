package id.latihan.java21.restfulapi.controller;

import id.latihan.java21.restfulapi.dto.WebResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "The User API. Contains all the operations that can be performed on a user.")
public class UserController {

    @GetMapping(value = "/api/user/hello",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> hello() {
        return WebResponse.<String>builder()
                .data("Hello")
                .build();
    }

}
