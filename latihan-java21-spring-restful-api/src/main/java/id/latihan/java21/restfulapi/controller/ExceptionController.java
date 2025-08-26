package id.latihan.java21.restfulapi.controller;

import id.latihan.java21.restfulapi.dto.WebResponse;
import id.latihan.java21.restfulapi.exception.ApplicationException;
import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    private static final String ERROR_MESSAGE_FORMAT = "%s: %s";

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public WebResponse<String> eap100(ApplicationException e) {
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , e.getMessage());
        log.error(message, e.getMessage());
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }

    @ExceptionHandler(value = InvalidKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public WebResponse<String> eap102(InvalidKeyException e) {
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , e.getMessage());
        log.error(message, e.getMessage());
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }


    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResponse<String> edb201(DataIntegrityViolationException e) {
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , e.getMostSpecificCause().getMessage());
        log.error(message, e.getMessage());
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResponse<String> edb202(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , String.join(",", errors));
        log.error(message, e);
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }

    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResponse<String> edb203(SQLSyntaxErrorException e) {
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , e.getCause().getMessage());
        log.error(message, e);
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }

    @ExceptionHandler(value = PropertyValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public WebResponse<String> edb204(PropertyValueException e) {
        String message = String.format(ERROR_MESSAGE_FORMAT, Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase()
                , e.getPropertyName());
        log.error(message, e);
        return WebResponse.<String>builder()
                .errors(message)
                .build();
    }
}
