package com.bartlomiejpluta.esa.error;

public class EsaException extends RuntimeException {

    public EsaException() {
        super();
    }

    public EsaException(String message) {
        super(message);
    }

    public EsaException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsaException(Throwable cause) {
        super(cause);
    }
}
