package bankingSystem.core;

public class InvalidCreditCardArgumentException extends IllegalArgumentException {
    public InvalidCreditCardArgumentException(String message) {
        super(message);
    }
}
