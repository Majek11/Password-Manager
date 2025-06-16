package dev.majek.passwordmanagement.exception;

public class DuplicateVaultItemException extends RuntimeException {
    public DuplicateVaultItemException(String message) {
        super(message);
    }
}
