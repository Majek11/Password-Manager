package dev.majek.passwordmanagement.exception;

public class VaultItemNotFoundException extends RuntimeException {
    public VaultItemNotFoundException(String message) {
        super(message);
    }
}
