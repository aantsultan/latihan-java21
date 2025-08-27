package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.exception.ApplicationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ApplicationException.class)
    public String applicationException(ApplicationException e) {
        return e.getMessage();
    }

}
