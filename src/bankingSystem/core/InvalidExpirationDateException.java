package bankingSystem.core;

import java.text.ParseException;

public class InvalidExpirationDateException extends ParseException {
    public InvalidExpirationDateException(String message, int errorOffset) {
        super(message, errorOffset);
    }
}
