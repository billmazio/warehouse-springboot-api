package gr.clothesmanager.service.exceptions;

public class UserNotAuthorizedException extends Exception {

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
