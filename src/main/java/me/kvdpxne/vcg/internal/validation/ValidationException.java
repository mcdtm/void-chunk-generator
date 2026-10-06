package me.kvdpxne.vcg.internal.validation;

/**
 * Wyjatek rzucany przy nieprawidlowej konfiguracji swiata.
 */
public final class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }
}