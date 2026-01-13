package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

public class UserNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "userNotFound";

    public UserNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    public UserNotFoundException(String code, String message) {
        super(code, message);
    }
}
