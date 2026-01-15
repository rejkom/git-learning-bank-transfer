package com.bankservice.exception;

/**
 * Wyjątek rzucany gdy IBAN jest nieprawidłowy
 */
public class InvalidIbanException extends RuntimeException {
    
    public InvalidIbanException(String message) {
        super(message);
    }
    
    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause);
    }
}
