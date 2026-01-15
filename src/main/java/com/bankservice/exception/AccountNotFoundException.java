package com.bankservice.exception;

/**
 * Wyjątek rzucany gdy konto nie zostało znalezione
 */
public class AccountNotFoundException extends RuntimeException {
    
    public AccountNotFoundException(String message) {
        super(message);
    }
    
    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
