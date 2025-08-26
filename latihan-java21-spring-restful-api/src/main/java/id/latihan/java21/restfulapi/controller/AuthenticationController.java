package id.latihan.java21.restfulapi.controller;

import id.latihan.java21.restfulapi.dto.UserDto;
import id.latihan.java21.restfulapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "The Authentication API. Contains all the operations that can be performed on authentication.")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping(value = "/api/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Register User", description = "Register User", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UserDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "Login example",
                                    value = """
                                                {
                                                  "username" : "aant_2",
                                                  "email" : "aant_2@mail.com",
                                                  "firstName": "A-Ant",
                                                  "lastName": "Sultan",
                                                  "password": "123",
                                                  "passwordConfirm" : "123"
                                                }
                                            """
                            )
                    }
            )
    ))
    public String register(@RequestBody UserDto userDto) {
        return userService.register(userDto);
    }

    @PostMapping(value = "/api/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Login User", description = "Login User", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UserDto.class),
                    examples = {
                            @ExampleObject(
                                    name = "Login example",
                                    value = """
                                                {
                                                  "username": "aant_2",
                                                  "password": "123"
                                                }
                                            """
                            )
                    }
            )
    ))
    public String login(@RequestBody UserDto userDto) {
        return userService.login(userDto);
    }

}
