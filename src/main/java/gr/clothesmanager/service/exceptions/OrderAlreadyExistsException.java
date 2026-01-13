package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class OrderAlreadyExistsException extends AppException {

    private static final String DEFAULT_CODE = "orderAlreadyExists";

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderAlreadyExistsException(String message) {
        super(DEFAULT_CODE, message);
    }

    public OrderAlreadyExistsException(String code, String message) {
        super(code, message);
    }
}

