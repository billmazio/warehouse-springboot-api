package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

public class UserAlreadyExistsException extends AppException {

    private static final String DEFAULT_CODE = "userNotFound";

    public UserAlreadyExistsException(String message) {
        super(DEFAULT_CODE, message);
    }

    public UserAlreadyExistsException(String code, String message) {
        super(code, message);
    }
}