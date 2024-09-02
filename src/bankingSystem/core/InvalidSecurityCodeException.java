package bankingSystem.core;

public class InvalidSecurityCodeException extends SecurityException {
    public InvalidSecurityCodeException(String message) {
        super(message);
    }
}
