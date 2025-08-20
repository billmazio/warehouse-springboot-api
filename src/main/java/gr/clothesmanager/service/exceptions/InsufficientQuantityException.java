package gr.clothesmanager.service.exceptions;

public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException (String message) {
        super(message);
    }
}