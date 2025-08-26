package id.latihan.java21.restfulapi.exception;

import java.io.Serial;

public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -69851505127851199L;

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }
}
