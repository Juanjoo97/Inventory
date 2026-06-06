package com.litethinking.inventory.domain.exception;

/** Se lanza cuando las credenciales de autenticacion no son validas. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
