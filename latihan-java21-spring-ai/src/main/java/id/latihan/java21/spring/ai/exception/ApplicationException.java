package id.latihan.java21.spring.ai.exception;

import java.io.Serial;

public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2206995948685244599L;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
