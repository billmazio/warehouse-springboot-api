package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class SizeNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "sizeNotFound";

    @Serial
    private static final long serialVersionUID = 1L;

    public SizeNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    public SizeNotFoundException(String code, String message) {
        super(code, message);
    }
}